package br.uff.graduatesapi.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine
import java.nio.charset.StandardCharsets

@Service
class EmailSenderService(
  private val emailSender: JavaMailSender,
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

  fun sendEmailHtmlTemplate(subject: String, targetEmail: String) {
    val message = emailSender.createMimeMessage()
    val helper = MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name())
    helper.setTo(targetEmail);
    helper.setSubject(subject);
    val context = Context()
    val html = templateEngine.process("email.html", context)
    helper.setText(html, true);

    emailSender.send(message)
  }
}