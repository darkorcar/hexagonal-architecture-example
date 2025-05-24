package com.example.hexagonal.domain.port.outbound

import com.example.hexagonal.domain.model.User

interface EmailService {
    fun sendWelcomeEmail(user: User)
    fun sendPromotionalEmail(user: User, content: String)
}