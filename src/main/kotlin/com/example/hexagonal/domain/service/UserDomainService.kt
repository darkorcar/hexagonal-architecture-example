package com.example.hexagonal.domain.service

import com.example.hexagonal.domain.model.User
import com.example.hexagonal.domain.model.UserEmail
import com.example.hexagonal.domain.port.outbound.EmailService
import com.example.hexagonal.domain.port.outbound.UserRepository
import com.example.hexagonal.domain.port.inbound.UserService
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserDomainService(
    private val userRepository: UserRepository,
    private val emailService: EmailService
) : UserService {

    override fun createUser(name: String, email: String, age: Int): User {
        val userEmail = UserEmail(email)

        // Check if user already exists
        userRepository.findByEmail(userEmail)?.let {
            throw IllegalArgumentException("User with email $email already exists")
        }

        val user = User(
            name = name,
            email = userEmail,
            age = age
        )

        val savedUser = userRepository.save(user)
        emailService.sendWelcomeEmail(savedUser)

        return savedUser
    }

    override fun getUserById(id: UUID): User? = userRepository.findById(id)

    override fun getUserByEmail(email: UserEmail): User? = userRepository.findByEmail(email)

    override fun getAllUsers(): List<User> = userRepository.findAll()

    override fun deleteUser(id: UUID): Boolean = userRepository.delete(id)

    override fun sendPromotionalEmailToEligibleUsers(content: String) {
        val eligibleUsers = userRepository.findAll()
            .filter { it.canReceivePromotionalEmails() }

        eligibleUsers.forEach { user ->
            emailService.sendPromotionalEmail(user, content)
        }
    }
}