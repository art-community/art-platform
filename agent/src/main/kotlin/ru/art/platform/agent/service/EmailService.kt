package ru.art.platform.agent.service

import ru.art.platform.agent.constants.SmtpConstants.MAIL_SMTP_HOST_PROPERTY
import ru.art.platform.agent.constants.SmtpConstants.MAIL_SMTP_PORT_PROPERTY
import ru.art.platform.agent.constants.SmtpConstants.SMTP_FROM_ENVIRONMENT
import ru.art.platform.agent.constants.SmtpConstants.SMTP_HOST_ENVIRONMENT
import ru.art.platform.agent.constants.SmtpConstants.SMTP_PORT_ENVIRONMENT
import ru.art.platform.agent.constants.SmtpConstants.SMTP_TO_ENVIRONMENT
import java.lang.System.getProperties
import java.lang.System.getenv
import javax.mail.Message.RecipientType.TO
import javax.mail.Session
import javax.mail.Transport.send
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailService {
    fun sendEmail(subject: String, text: String) {
        val to = getenv(SMTP_TO_ENVIRONMENT) ?: return
        val from = getenv(SMTP_FROM_ENVIRONMENT) ?: return
        val host = getenv(SMTP_HOST_ENVIRONMENT) ?: return
        val port = getenv(SMTP_PORT_ENVIRONMENT) ?: return
        val properties = getProperties()
        properties.setProperty(MAIL_SMTP_HOST_PROPERTY, host)
        properties.setProperty(MAIL_SMTP_PORT_PROPERTY, port)
        val session = Session.getDefaultInstance(properties);
        val message = MimeMessage(session)
        message.setFrom(InternetAddress(from))
        message.addRecipient(TO, InternetAddress(to))
        message.subject = subject
        message.setText(text)
        send(message)
    }
}