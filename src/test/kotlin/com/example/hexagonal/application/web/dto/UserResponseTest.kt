package com.example.hexagonal.application.web.dto

import com.example.hexagonal.application.web.model.response.UserResponse
import com.example.hexagonal.domain.model.User
import com.example.hexagonal.domain.model.UserEmail
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.util.UUID

class UserResponseTest : FunSpec({

    context("UserResponse") {

        test("correctly map all fields from User domain object to UserResponse") {
            val userId = UUID.randomUUID()
            val userName = "John Doe"
            val userEmail = "john.doe@example.com"
            val age = 1
            val createdAt = LocalDateTime.now()
            val domainUser = User(
                id = userId,
                name = userName,
                email = UserEmail(userEmail),
                age = age,
                createdAt = createdAt,
            )

            val userResponse = UserResponse.from(domainUser)

            userResponse.id shouldBe userId
            userResponse.name shouldBe userName
            userResponse.email shouldBe userEmail
            userResponse.age shouldBe age
            userResponse.createdAt shouldBe createdAt
        }
    }

    context("User age mapping") {
        //Given
        data class AgeTestCase(val age: Int, val expectedIsAdult: Boolean)

        withData(
            AgeTestCase(age = 1, expectedIsAdult = false),
            AgeTestCase(age = 17, expectedIsAdult = false),
            AgeTestCase(age = 18, expectedIsAdult = true),
            AgeTestCase(age = 19, expectedIsAdult = true),
            AgeTestCase(age = 65, expectedIsAdult = true),
        ) { testCase ->
            val userId = UUID.randomUUID()
            val userName = "Test User"
            val userEmail = "test.user@example.com"
            val createdAt = LocalDateTime.now()
            val domainUser = User(
                id = userId,
                name = userName,
                email = UserEmail(userEmail),
                age = testCase.age,
                createdAt = createdAt,
            )

            // When
            val userResponse = UserResponse.from(domainUser)

            // Then
            userResponse.isAdult shouldBe testCase.expectedIsAdult
        }
    }
})
