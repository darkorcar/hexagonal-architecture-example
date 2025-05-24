package com.example.hexagonal

import com.example.hexagonal.application.web.model.request.CreateUserRequest
import com.example.hexagonal.application.web.model.response.UserResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserIntegrationTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) : DescribeSpec({

    extension(SpringExtension)

    describe("User API Integration Tests") {

        describe("POST /api/users") {
            it("should create a new user") {
                // Given
                val request = CreateUserRequest("John Doe", "john@example.com", 25)
                val requestJson = objectMapper.writeValueAsString(request)

                // When & Then
                val result = mockMvc.perform(
                    post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                    .andExpect(status().isCreated)
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.email").value("john@example.com"))
                    .andExpect(jsonPath("$.age").value(25))
                    .andExpect(jsonPath("$.isAdult").value(true))
                    .andExpect(jsonPath("$.id").exists())
                    .andReturn()

                val responseJson = result.response.contentAsString
                val userResponse = objectMapper.readValue(responseJson, UserResponse::class.java)
                userResponse.id shouldNotBe null
            }

            it("should return bad request for invalid email") {
                // Given
                val request = CreateUserRequest("John Doe", "invalid-email", 25)
                val requestJson = objectMapper.writeValueAsString(request)

                // When & Then
                mockMvc.perform(
                    post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                    .andExpect(status().isBadRequest)
            }

            it("should return bad request for duplicate email") {
                // Given
                val request1 = CreateUserRequest("John Doe", "duplicate@example.com", 25)
                val request2 = CreateUserRequest("Jane Doe", "duplicate@example.com", 30)
                val requestJson1 = objectMapper.writeValueAsString(request1)
                val requestJson2 = objectMapper.writeValueAsString(request2)

                // Create first user
                mockMvc.perform(
                    post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson1)
                )
                    .andExpect(status().isCreated)

                // Try to create second user with same email
                mockMvc.perform(
                    post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson2)
                )
                    .andExpect(status().isBadRequest)
            }
        }

        describe("GET /api/users") {
            it("should return all users") {
                // Given - create a user first
                val request = CreateUserRequest("Test User", "test@example.com", 30)
                val requestJson = objectMapper.writeValueAsString(request)

                mockMvc.perform(
                    post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                    .andExpect(status().isCreated)

                // When & Then
                mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$").isArray)
                    .andExpect(jsonPath("$[?(@.email=='test@example.com')]").exists())
            }
        }

        describe("GET /api/users/by-email") {
            it("should return user by email") {
                // Given - create a user first
                val request = CreateUserRequest("Email User", "email@example.com", 28)
                val requestJson = objectMapper.writeValueAsString(request)

                mockMvc.perform(
                    post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                    .andExpect(status().isCreated)

                // When & Then
                mockMvc.perform(
                    get("/api/users/by-email")
                        .param("email", "email@example.com")
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.name").value("Email User"))
                    .andExpect(jsonPath("$.email").value("email@example.com"))
            }

            it("should return not found for non-existent email") {
                mockMvc.perform(
                    get("/api/users/by-email")
                        .param("email", "nonexistent@example.com")
                )
                    .andExpect(status().isNotFound)
            }
        }
    }
})