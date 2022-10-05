package br.uff.graduatesapi.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailSenderService(
  private val emailSender: JavaMailSender,
  private val template: SimpleMailMessage,
) {
  fun sendEmail(subject: String, text: String, targetEmail: String) {
    val message = SimpleMailMessage()

    message.setSubject(subject)
    message.setText(text)
    message.setTo(targetEmail)

    emailSender.send(message)
  }

  fun  sendEmailUsingTemplate(name: String, targetEmail: String) {
    val message = SimpleMailMessage(template)
    val text = template.text
    message.setText(text!!.format(name))
    message.setTo(targetEmail)

    emailSender.send(message)
  }
}