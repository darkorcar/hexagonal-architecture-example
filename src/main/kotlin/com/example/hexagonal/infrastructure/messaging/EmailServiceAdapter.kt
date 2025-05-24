package com.example.hexagonal.infrastructure.messaging

import com.example.hexagonal.domain.model.User
import com.example.hexagonal.domain.port.outbound.EmailService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EmailServiceAdapter : EmailService {

    private val logger = LoggerFactory.getLogger(EmailServiceAdapter::class.java)

    override fun sendWelcomeEmail(user: User) {
        // In a real implementation, this would integrate with an email service
        logger.info("Sending welcome email to ${user.email.value} for user ${user.name}")

        // Simulate email sending
        Thread.sleep(100)

        logger.info("Welcome email sent successfully to ${user.email.value}")
    }

    override fun sendPromotionalEmail(user: User, content: String) {
        logger.info("Sending promotional email to ${user.email.value}")
        logger.debug("Email content: $content")

        // Simulate email sending
        Thread.sleep(50)

        logger.info("Promotional email sent successfully to ${user.email.value}")
    }
}