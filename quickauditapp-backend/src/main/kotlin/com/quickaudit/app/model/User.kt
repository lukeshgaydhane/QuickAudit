package com.quickaudit.app.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true, nullable = false)
    val username: String,
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    val password: String,
    
    @field:Email(message = "Email should be valid")
    @Column(unique = true)
    val email: String? = null,
    
    @field:Size(max = 100, message = "Full name must not exceed 100 characters")
    val fullName: String? = null,
    
    @field:Size(max = 20, message = "Phone number must not exceed 20 characters")
    val phoneNumber: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole = UserRole.USER,
    
    @Column(nullable = false)
    val isActive: Boolean = true,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val lastLoginAt: LocalDateTime? = null,
    
    @field:Size(max = 500, message = "Profile description must not exceed 500 characters")
    val profileDescription: String? = null,
    
    val profileImageUrl: String? = null
)

enum class UserRole {
    USER, ADMIN, AUDITOR, MANAGER
} 