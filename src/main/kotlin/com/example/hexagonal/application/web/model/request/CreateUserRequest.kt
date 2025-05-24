package com.example.hexagonal.application.web.model.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class CreateUserRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:Email(message = "Email must be valid")
    @field:NotBlank(message = "Email is required")
    val email: String,

    @field:Min(value = 0, message = "Age must be non-negative")
    @field:Max(value = 150, message = "Age must not exceed 150")
    val age: Int
)