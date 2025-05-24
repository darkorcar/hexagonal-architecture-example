package com.example.hexagonal.domain.model

import java.time.LocalDateTime
import java.util.*

data class User(
    val id: UUID? = null,
    val name: String,
    val email: UserEmail,
    val age: Int,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(name.isNotBlank()) { "User name cannot be blank" }
        require(age >= 0) { "User age cannot be negative" }
        require(age <= 150) { "User age cannot exceed 150" }
    }

    fun isAdult(): Boolean = age >= 18

    fun canReceivePromotionalEmails(): Boolean = isAdult()
}