package com.example.hexagonal.application.web.model.response

import com.example.hexagonal.domain.model.User
import java.time.LocalDateTime
import java.util.UUID

data class UserResponse(
    val id: UUID?,
    val name: String,
    val email: String,
    val age: Int,
    val isAdult: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(user: User) = UserResponse(
            id = user.id,
            name = user.name,
            email = user.email.value,
            age = user.age,
            isAdult = user.isAdult(),
            createdAt = user.createdAt
        )
    }
}