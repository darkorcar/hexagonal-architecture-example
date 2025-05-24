package com.example.hexagonal.infrastructure.persistence

import com.example.hexagonal.domain.model.User
import com.example.hexagonal.domain.model.UserEmail
import com.example.hexagonal.domain.port.outbound.UserRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UserRepositoryAdapter(
    private val jpaRepository: UserJpaRepository
) : UserRepository {

    override fun save(user: User): User {
        val entity = user.toEntity()
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findById(id: UUID): User? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByEmail(email: UserEmail): User? {
        return jpaRepository.findByEmail(email.value)?.toDomain()
    }

    override fun findAll(): List<User> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun delete(id: UUID): Boolean {
        return if (jpaRepository.existsById(id)) {
            jpaRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    private fun User.toEntity() = UserEntity(
        id = this.id,
        name = this.name,
        email = this.email.value,
        age = this.age,
        createdAt = this.createdAt
    )

    private fun UserEntity.toDomain() = User(
        id = this.id,
        name = this.name,
        email = UserEmail(this.email),
        age = this.age,
        createdAt = this.createdAt
    )
}