package com.example.hexagonal

import com.example.hexagonal.application.web.model.request.CreateUserRequest
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
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
class FullApplicationIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    init { // Use init block for DescribeSpec
        describe("Full Application Integration Tests") {

            it("should handle complete user lifecycle") {
                // Create adult user
                val adultRequest = CreateUserRequest("Adult User", "adult@example.com", 25)
                val adultJson = objectMapper.writeValueAsString(adultRequest)

                mockMvc.perform( // mockMvc will be initialized by Spring
                    post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adultJson)
                )
                    .andExpect(status().isCreated)
                    .andExpect(jsonPath("$.isAdult").value(true))
                    .andReturn()

                // Create minor user
                val minorRequest = CreateUserRequest("Minor User", "minor@example.com", 16)
                val minorJson = objectMapper.writeValueAsString(minorRequest)

                mockMvc.perform(
                    post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(minorJson)
                )
                    .andExpect(status().isCreated)
                    .andExpect(jsonPath("$.isAdult").value(false))

                // Get all users
                mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.length()").value(2))

                // Send promotional emails (should only send to adults)
                mockMvc.perform(
                    post("/api/users/promotional-emails")
                        .param("content", "Special offer!")
                )
                    .andExpect(status().isOk)

                // Get user by email
                mockMvc.perform(
                    get("/api/users/by-email")
                        .param("email", "adult@example.com")
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.name").value("Adult User"))
            }

            it("should validate business rules") {
                // Test invalid age
                val invalidAgeRequest = CreateUserRequest("Invalid User", "invalid@example.com", -5)
                val invalidAgeJson = objectMapper.writeValueAsString(invalidAgeRequest)

                mockMvc.perform(
                    post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAgeJson)
                )
                    .andExpect(status().isBadRequest)

                // Test invalid email format
                val invalidEmailRequest = CreateUserRequest("Invalid Email", "not-an-email", 25)
                val invalidEmailJson = objectMapper.writeValueAsString(invalidEmailRequest)

                mockMvc.perform(
                    post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmailJson)
                )
                    .andExpect(status().isBadRequest)
            }
        }
    }
}