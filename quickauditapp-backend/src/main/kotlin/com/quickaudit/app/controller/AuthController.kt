package com.quickaudit.app.controller

import com.quickaudit.app.dto.*
import com.quickaudit.app.security.JwtUtil
import com.quickaudit.app.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtUtil: JwtUtil
) {
    
    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val authResponse = authService.login(loginRequest)
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "Login successful",
                data = authResponse
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = e.message ?: "Login failed",
                errors = listOf(e.message ?: "Invalid credentials")
            ))
        }
    }
    
    @PostMapping("/signup")
    fun signup(@Valid @RequestBody signupRequest: SignupRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val authResponse = authService.signup(signupRequest)
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "User registered successfully",
                data = authResponse
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = e.message ?: "Registration failed",
                errors = listOf(e.message ?: "Registration failed")
            ))
        }
    }
    
    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val authResponse = authService.refreshToken(refreshTokenRequest)
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "Token refreshed successfully",
                data = authResponse
            ))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = e.message ?: "Token refresh failed",
                errors = listOf(e.message ?: "Invalid refresh token")
            ))
        }
    }
    
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<ApiResponse<Nothing>> {
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            val userId = jwtUtil.extractUserId(token)
            
            if (userId != null) {
                authService.logout(userId)
            }
        }
        
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "Logout successful"
        ))
    }
    
    @PostMapping("/change-password")
    fun changePassword(
        @Valid @RequestBody changePasswordRequest: ChangePasswordRequest,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = "Authentication required",
                errors = listOf("Valid token required")
            ))
        }
        
        val token = authHeader.substring(7)
        val userId = jwtUtil.extractUserId(token)
        
        if (userId == null) {
            return ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = "Invalid token",
                errors = listOf("Could not extract user ID from token")
            ))
        }
        
        return try {
            authService.changePassword(userId, changePasswordRequest)
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "Password changed successfully"
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = e.message ?: "Password change failed",
                errors = listOf(e.message ?: "Password change failed")
            ))
        }
    }
    
    @PostMapping("/forgot-password")
    fun forgotPassword(@Valid @RequestBody forgotPasswordRequest: ForgotPasswordRequest): ResponseEntity<ApiResponse<Nothing>> {
        return try {
            val success = authService.forgotPassword(forgotPasswordRequest)
            if (success) {
                ResponseEntity.ok(ApiResponse(
                    success = true,
                    message = "Password reset instructions sent to your email"
                ))
            } else {
                ResponseEntity.ok(ApiResponse(
                    success = true,
                    message = "If the email exists, password reset instructions have been sent"
                ))
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = e.message ?: "Password reset request failed",
                errors = listOf(e.message ?: "Password reset request failed")
            ))
        }
    }
    
    @PostMapping("/reset-password")
    fun resetPassword(@Valid @RequestBody resetPasswordRequest: ResetPasswordRequest): ResponseEntity<ApiResponse<Nothing>> {
        return try {
            authService.resetPassword(resetPasswordRequest)
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "Password reset successfully"
            ))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = e.message ?: "Password reset failed",
                errors = listOf(e.message ?: "Password reset failed")
            ))
        }
    }
    
    @GetMapping("/me")
    fun getCurrentUser(request: HttpServletRequest): ResponseEntity<ApiResponse<UserInfo>> {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = "Authentication required",
                errors = listOf("Valid token required")
            ))
        }
        
        val token = authHeader.substring(7)
        val userInfo = authService.getCurrentUser(token)
        
        return if (userInfo != null) {
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "User information retrieved successfully",
                data = userInfo
            ))
        } else {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = "Invalid token",
                errors = listOf("Could not extract user information from token")
            ))
        }
    }
    
    @GetMapping("/validate")
    fun validateToken(request: HttpServletRequest): ResponseEntity<ApiResponse<Nothing>> {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = "Authentication required",
                errors = listOf("Valid token required")
            ))
        }
        
        val token = authHeader.substring(7)
        val isValid = authService.validateToken(token)
        
        return if (isValid) {
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "Token is valid"
            ))
        } else {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = "Token is invalid or expired",
                errors = listOf("Token validation failed")
            ))
        }
    }
} 