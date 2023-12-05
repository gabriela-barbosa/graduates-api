package br.uff.graduatesapi.service

import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
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
	fun sendEmail(subject: String, text: String, targetEmail: String) {
		val message = SimpleMailMessage()

		message.setSubject(subject)
		message.setText(text)
		message.setTo(targetEmail)

		emailSender.send(message)
	}

	fun sendEmailUsingTemplate(name: String, targetEmail: String) {
		val message = SimpleMailMessage(template)
		val text = template.text
		message.setText(text!!.format(name))
		message.setTo(targetEmail)

		emailSender.send(message)
	}

	private fun setupEmailTemplate(subject: String, emailContentId: UUID): Pair<MimeMessageHelper, MimeMessage> {
		val emailContent = when (val result = emailService.findEmailById(emailContentId)) {
			is ResponseResult.Success -> result.data!!
			is ResponseResult.Error -> throw Exception("Invalid email content")
		}

		val emailFields = mapOf(
			"content" to emailContent.content,
			"buttonText" to emailContent.buttonText,
			"buttonLink" to emailContent.buttonURL
		)

		val message = emailSender.createMimeMessage()
		val helper =
			MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name())
		helper.setSubject(subject)
		val context = Context()
		context.setVariables(emailFields)
		val html = templateEngine.process("email.html", context)
		helper.setText(html, true)

		return Pair(helper, message)
	}

	fun sendEmailsHtmlTemplate(
		subject: String,
		userIds: List<UUID>,
		emailContentId: UUID
	): ResponseResult<Nothing?> {

		val users = userService.findByIds(userIds)

		val (helper, message) = setupEmailTemplate(subject, emailContentId)

		if (users is ResponseResult.Error) {
			return ResponseResult.Error(Errors.USER_NOT_FOUND)
		}

		users.data!!.forEach {
			helper.setTo(it.email)
			try {
				emailSender.send(message)
			} catch (e: Exception) {
				println(e.message)
			}
		}

		return ResponseResult.Success(null)

	}

	fun sendEmailHtmlTemplate(subject: String, targetEmail: String, emailContentId: UUID): ResponseResult<Nothing?> {
		val (helper, message) = setupEmailTemplate(subject, emailContentId)
		helper.setTo(targetEmail)
		emailSender.send(message)
		return ResponseResult.Success(null)
	}
}