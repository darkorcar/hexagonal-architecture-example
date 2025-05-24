package com.example.hexagonal.domain.port.outbound

import com.example.hexagonal.domain.model.User
import com.example.hexagonal.domain.model.UserEmail
import java.util.UUID

interface UserRepository {
    fun save(user: User): User
    fun findById(id: UUID): User?
    fun findByEmail(email: UserEmail): User?
    fun findAll(): List<User>
    fun delete(id: UUID): Boolean
}