package com.quickaudit.app.dto

import com.quickaudit.app.model.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:NotBlank(message = "Username is required")
    val username: String,
    
    @field:NotBlank(message = "Password is required")
    val password: String
)

data class SignupRequest(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String,
    
    @field:Email(message = "Email should be valid")
    val email: String? = null,
    
    @field:Size(max = 100, message = "Full name must not exceed 100 characters")
    val fullName: String? = null,
    
    @field:Size(max = 20, message = "Phone number must not exceed 20 characters")
    val phoneNumber: String? = null,
    
    val role: UserRole = UserRole.USER,
    
    @field:Size(max = 500, message = "Profile description must not exceed 500 characters")
    val profileDescription: String? = null
)

data class AuthResponse(
    val token: String,
    val refreshToken: String? = null,
    val user: UserInfo,
    val expiresIn: Long
)

data class UserInfo(
    val id: Long,
    val username: String,
    val email: String?,
    val fullName: String?,
    val role: UserRole,
    val isActive: Boolean
)

data class RefreshTokenRequest(
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String
)

data class ChangePasswordRequest(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,
    
    @field:NotBlank(message = "New password is required")
    @field:Size(min = 6, message = "New password must be at least 6 characters")
    val newPassword: String
)

data class ForgotPasswordRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    val email: String
)

data class ResetPasswordRequest(
    @field:NotBlank(message = "Reset token is required")
    val resetToken: String,
    
    @field:NotBlank(message = "New password is required")
    @field:Size(min = 6, message = "New password must be at least 6 characters")
    val newPassword: String
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errors: List<String>? = null
) 