package com.example.hexagonal.application

import com.example.hexagonal.application.web.UserController
import com.example.hexagonal.application.web.model.request.CreateUserRequest
import com.example.hexagonal.domain.model.User
import com.example.hexagonal.domain.model.UserEmail
import com.example.hexagonal.domain.port.inbound.UserService
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.HttpStatus
import java.util.*

class UserControllerTest : DescribeSpec({

    describe("UserController") {
        val userService = mockk<UserService>()
        val controller = UserController(userService)

        beforeEach {
            clearAllMocks()
        }

        describe("createUser") {
            it("should create user successfully") {
                // Given
                val request = CreateUserRequest("John Doe", "john@example.com", 25)
                val createdUser = User(
                    id = UUID.randomUUID(),
                    name = request.name,
                    email = UserEmail(request.email),
                    age = request.age
                )

                every {
                    userService.createUser(request.name, request.email, request.age)
                } returns createdUser

                // When
                val response = controller.createUser(request)

                // Then
                response.statusCode shouldBe HttpStatus.CREATED
                response.body?.name shouldBe request.name
                response.body?.email shouldBe request.email
                response.body?.age shouldBe request.age

                verify { userService.createUser(request.name, request.email, request.age) }
            }

            it("should return bad request when user creation fails") {
                // Given
                val request = CreateUserRequest("John Doe", "john@example.com", 25)

                every {
                    userService.createUser(request.name, request.email, request.age)
                } throws IllegalArgumentException("User already exists")

                // When
                val response = controller.createUser(request)

                // Then
                response.statusCode shouldBe HttpStatus.BAD_REQUEST
                response.body shouldBe null

                verify { userService.createUser(request.name, request.email, request.age) }
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

                every { userService.getUserById(userId) } returns user

                // When
                val response = controller.getUserById(userId)

                // Then
                response.statusCode shouldBe HttpStatus.OK
                response.body?.id shouldBe userId
                response.body?.name shouldBe user.name

                verify { userService.getUserById(userId) }
            }

            it("should return not found when user doesn't exist") {
                // Given
                val userId = UUID.randomUUID()

                every { userService.getUserById(userId) } returns null

                // When
                val response = controller.getUserById(userId)

                // Then
                response.statusCode shouldBe HttpStatus.NOT_FOUND
                response.body shouldBe null

                verify { userService.getUserById(userId) }
            }
        }
    }
})