package com.example.hexagonal.application.web

import com.example.hexagonal.application.web.model.request.CreateUserRequest
import com.example.hexagonal.application.web.model.response.UserResponse
import com.example.hexagonal.domain.model.UserEmail
import com.example.hexagonal.domain.port.inbound.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        return try {
            val user = userService.createUser(request.name, request.email, request.age)
            ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<UserResponse> {
        val user = userService.getUserById(id)
        return if (user != null) {
            ResponseEntity.ok(UserResponse.from(user))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserResponse>> {
        val users = userService.getAllUsers().map { UserResponse.from(it) }
        return ResponseEntity.ok(users)
    }

    @GetMapping("/by-email")
    fun getUserByEmail(@RequestParam email: String): ResponseEntity<UserResponse> {
        return try {
            val userEmail = UserEmail(email)
            val user = userService.getUserByEmail(userEmail)
            if (user != null) {
                ResponseEntity.ok(UserResponse.from(user))
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> {
        val deleted = userService.deleteUser(id)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/promotional-emails")
    fun sendPromotionalEmails(@RequestParam content: String): ResponseEntity<Void> {
        userService.sendPromotionalEmailToEligibleUsers(content)
        return ResponseEntity.ok().build()
    }
}