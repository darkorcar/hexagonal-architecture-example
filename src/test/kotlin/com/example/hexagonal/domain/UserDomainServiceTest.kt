package com.example.hexagonal.domain

import com.example.hexagonal.domain.model.User
import com.example.hexagonal.domain.model.UserEmail
import com.example.hexagonal.domain.port.outbound.EmailService
import com.example.hexagonal.domain.port.outbound.UserRepository
import com.example.hexagonal.domain.service.UserDomainService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import java.util.*

class UserDomainServiceTest : DescribeSpec({

    describe("UserDomainService") {
        val userRepository = mockk<UserRepository>()
        val emailService = mockk<EmailService>()
        val userService = UserDomainService(userRepository, emailService)

        beforeEach {
            clearAllMocks()
        }

        describe("createUser") {
            it("should create and save a new user") {
                // Given
                val name = "John Doe"
                val email = "john@example.com"
                val age = 25
                val savedUser = User(
                    id = UUID.randomUUID(),
                    name = name,
                    email = UserEmail(email),
                    age = age
                )

                every { userRepository.findByEmail(UserEmail(email)) } returns null
                every { userRepository.save(any()) } returns savedUser
                every { emailService.sendWelcomeEmail(any()) } just Runs

                // When
                val result = userService.createUser(name, email, age)

                // Then
                result shouldBe savedUser
                verify { userRepository.findByEmail(UserEmail(email)) }
                verify { userRepository.save(any()) }
                verify { emailService.sendWelcomeEmail(savedUser) }
            }

            it("should throw exception when user already exists") {
                // Given
                val name = "John Doe"
                val email = "john@example.com"
                val age = 25
                val existingUser = User(
                    id = UUID.randomUUID(),
                    name = "Existing User",
                    email = UserEmail(email),
                    age = 30
                )

                every { userRepository.findByEmail(UserEmail(email)) } returns existingUser

                // When & Then
                shouldThrow<IllegalArgumentException> {
                    userService.createUser(name, email, age)
                }

                verify { userRepository.findByEmail(UserEmail(email)) }
                verify(exactly = 0) { userRepository.save(any()) }
                verify(exactly = 0) { emailService.sendWelcomeEmail(any()) }
            }
        }

        describe("getUserById") {
            it("should return user when found") {
                // Given
                val userId = UUID.randomUUID()
                val user = User(
                    id = userId,
                    name = "John Doe",
                    email = UserEmail("john@example.com"),
                    age = 25
                )

                every { userRepository.findById(userId) } returns user

                // When
                val result = userService.getUserById(userId)

                // Then
                result shouldBe user
                verify { userRepository.findById(userId) }
            }

            it("should return null when user not found") {
                // Given
                val userId = UUID.randomUUID()

                every { userRepository.findById(userId) } returns null

                // When
                val result = userService.getUserById(userId)

                // Then
                result shouldBe null
                verify { userRepository.findById(userId) }
            }
        }

        describe("sendPromotionalEmailToEligibleUsers") {
            it("should send emails only to adult users") {
                // Given
                val adultUser = User(
                    id = UUID.randomUUID(),
                    name = "Adult User",
                    email = UserEmail("adult@example.com"),
                    age = 25
                )
                val minorUser = User(
                    id = UUID.randomUUID(),
                    name = "Minor User",
                    email = UserEmail("minor@example.com"),
                    age = 16
                )
                val content = "Special promotion!"

                every { userRepository.findAll() } returns listOf(adultUser, minorUser)
                every { emailService.sendPromotionalEmail(any(), any()) } just Runs

                // When
                userService.sendPromotionalEmailToEligibleUsers(content)

                // Then
                verify { userRepository.findAll() }
                verify { emailService.sendPromotionalEmail(adultUser, content) }
                verify(exactly = 0) { emailService.sendPromotionalEmail(minorUser, content) }
            }
        }
    }
})