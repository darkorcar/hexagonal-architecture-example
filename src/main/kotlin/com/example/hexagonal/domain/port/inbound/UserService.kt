package com.example.hexagonal.domain.port.inbound

import com.example.hexagonal.domain.model.User
import com.example.hexagonal.domain.model.UserEmail
import java.util.UUID

interface UserService {
    fun createUser(name: String, email: String, age: Int): User
    fun getUserById(id: UUID): User?
    fun getUserByEmail(email: UserEmail): User?
    fun getAllUsers(): List<User>
    fun deleteUser(id: UUID): Boolean
    fun sendPromotionalEmailToEligibleUsers(content: String)
}