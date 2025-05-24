package com.example.hexagonal

import com.example.hexagonal.domain.model.User
import com.example.hexagonal.domain.model.UserEmail
import com.example.hexagonal.domain.port.outbound.UserRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryIntegrationTest(
    private val userRepository: UserRepository
) : DescribeSpec({

    extension(SpringExtension)

    describe("UserRepository Integration Tests") {

        describe("save and findById") {
            it("should save and retrieve user") {
                // Given
                val user = User(
                    name = "John Doe",
                    email = UserEmail("john@example.com"),
                    age = 25
                )

                // When
                val savedUser = userRepository.save(user)
                val retrievedUser = userRepository.findById(savedUser.id!!)

                // Then
                savedUser.id shouldNotBe null
                retrievedUser shouldNotBe null
                retrievedUser?.name shouldBe "John Doe"
                retrievedUser?.email?.value shouldBe "john@example.com"
                retrievedUser?.age shouldBe 25
            }
        }

        describe("findByEmail") {
            it("should find user by email") {
                // Given
                val user = User(
                    name = "Jane Doe",
                    email = UserEmail("jane@example.com"),
                    age = 30
                )

                // When
                userRepository.save(user)
                val foundUser = userRepository.findByEmail(UserEmail("jane@example.com"))

                // Then
                foundUser shouldNotBe null
                foundUser?.name shouldBe "Jane Doe"
                foundUser?.email?.value shouldBe "jane@example.com"
            }

            it("should return null for non-existent email") {
                // When
                val foundUser = userRepository.findByEmail(UserEmail("nonexistent@example.com"))

                // Then
                foundUser shouldBe null
            }
        }

        describe("delete") {
            it("should delete user successfully") {
                // Given
                val user = User(
                    name = "Delete User",
                    email = UserEmail("delete@example.com"),
                    age = 35
                )
                val savedUser = userRepository.save(user)

                // When
                val deleted = userRepository.delete(savedUser.id!!)
                val retrievedUser = userRepository.findById(savedUser.id!!)

                // Then
                deleted shouldBe true
                retrievedUser shouldBe null
            }

            it("should return false when deleting non-existent user") {
                // Given
                val nonExistentId = UUID.randomUUID()

                // When
                val deleted = userRepository.delete(nonExistentId)

                // Then
                deleted shouldBe false
            }
        }

        describe("findAll") {
            it("should return all users") {
                // Given
                val user1 = User(name = "User 1", email = UserEmail("user1@example.com"), age = 25)
                val user2 = User(name = "User 2", email = UserEmail("user2@example.com"), age = 30)

                // When
                userRepository.save(user1)
                userRepository.save(user2)
                val allUsers = userRepository.findAll()

                // Then
                allUsers.size shouldBe 2
                allUsers.map { it.name } shouldBe listOf("User 1", "User 2")
            }
        }
    }
})