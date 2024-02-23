package br.uff.graduatesapi.service

import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.error.passError
import org.springframework.beans.factory.annotation.Value
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
    private val resetPasswordCodeService: ResetPasswordCodeService,
    @Value("\${frontend.url:default}")
    private val frontendUrl: String = ""
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

    fun sendResetPasswordEmail(email: String): ResponseResult<Nothing?> {
        val passwordCode = when (val result = resetPasswordCodeService.createPasswordCodeByEmail(email)) {
            is ResponseResult.Success -> result.data!!
            is ResponseResult.Error -> return result.passError()
        }

        val text = "Acesse o link para redefinir sua senha: $frontendUrl/reset-password/${passwordCode.id}. Esse link expira em 24 horas."
        return sendEmail("Redefinição de senha", text, email)
    }
}