package br.uff.graduatesapi.service

import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.error.passError
import org.springframework.http.HttpStatus
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine
import java.nio.charset.StandardCharsets
import java.util.*
import javax.mail.internet.MimeMessage

@Service
class EmailSenderService(
	private val emailSender: JavaMailSender,
	private val emailService: EmailService,
	private val userService: UserService,
	private val template: SimpleMailMessage,
	private val templateEngine: SpringTemplateEngine,

	) {
	fun sendEmail(subject: String, text: String, targetEmail: String): ResponseResult<Nothing?> {
		return try {
			val message = SimpleMailMessage()

			message.setSubject(subject)
			message.setText(text)
			message.setTo(targetEmail)

			emailSender.send(message)
			ResponseResult.Success(null)
		} catch (e: Exception) {
			ResponseResult.Error(Errors.EMAIL_NOT_SENT)
		}
	}

	private fun setupEmailTemplate(emailContentId: UUID): ResponseResult<Pair<MimeMessageHelper, MimeMessage>> {
		val emailContent = when (val result = emailService.findEmailById(emailContentId)) {
			is ResponseResult.Success -> result.data!!
			is ResponseResult.Error -> return ResponseResult.Error(
				Errors.EMAIL_NOT_FOUND,
				errorCode = HttpStatus.UNPROCESSABLE_ENTITY,
			)
		}

		val emailFields = mapOf(
			"content" to emailContent.content,
			"buttonText" to emailContent.buttonText,
			"buttonLink" to emailContent.buttonURL
		)

		val message = emailSender.createMimeMessage()
		val helper =
			MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name())
		helper.setSubject(emailContent.title)
		val context = Context()
		context.setVariables(emailFields)
		val html = templateEngine.process("email.html", context)
		helper.setText(html, true)

		return ResponseResult.Success(Pair(helper, message))
	}

	fun sendEmailsHtmlTemplate(
		userIds: List<UUID>,
		emailContentId: UUID
	): ResponseResult<Nothing?> {

		val users = when (val result = userService.findByIds(userIds)) {
			is ResponseResult.Success -> result.data!!
			is ResponseResult.Error -> return result.passError(HttpStatus.UNPROCESSABLE_ENTITY)
		}


		val (helper, message) = when (val result = setupEmailTemplate(emailContentId)) {
			is ResponseResult.Success -> result.data!!
			is ResponseResult.Error -> return result.passError(HttpStatus.UNPROCESSABLE_ENTITY)
		}

		val emails = users.map { it.email }.toTypedArray()

		try {
			helper.setBcc(emails)
			emailSender.send(message)
		} catch (e: Exception) {
			return ResponseResult.Error(Errors.EMAIL_NOT_SENT)
		}

		return ResponseResult.Success(null)

	}

	fun sendEmailHtmlTemplate(subject: String, targetEmail: String, emailContentId: UUID): ResponseResult<Nothing?> {
		val (helper, message) = when (val result = setupEmailTemplate(emailContentId)) {
			is ResponseResult.Success -> result.data!!
			is ResponseResult.Error -> return result.passError(HttpStatus.UNPROCESSABLE_ENTITY)
		}
		helper.setTo(targetEmail)
		emailSender.send(message)
		return ResponseResult.Success(null)
	}
}