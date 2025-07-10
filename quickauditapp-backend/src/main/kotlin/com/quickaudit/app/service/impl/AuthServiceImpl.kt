package com.quickaudit.app.service.impl

import com.quickaudit.app.dto.*
import com.quickaudit.app.model.User
import com.quickaudit.app.security.JwtUtil
import com.quickaudit.app.service.AuthService
import com.quickaudit.app.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class AuthServiceImpl(
    private val userService: UserService,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder
) : AuthService {
    
    override fun login(loginRequest: LoginRequest): AuthResponse {
        val user = userService.authenticateUser(loginRequest.username, loginRequest.password)
            ?: throw IllegalArgumentException("Invalid username or password")
        
        val token = jwtUtil.generateToken(user)
        val refreshToken = generateRefreshToken()
        
        return AuthResponse(
            token = token,
            refreshToken = refreshToken,
            user = mapToUserInfo(user),
            expiresIn = 86400000 // 24 hours
        )
    }
    
    override fun signup(signupRequest: SignupRequest): AuthResponse {
        // Check if username already exists
        userService.getUserByUsername(signupRequest.username)?.let {
            throw IllegalArgumentException("Username already exists")
        }
        
        // Check if email already exists
        signupRequest.email?.let { email ->
            userService.getUserByEmail(email)?.let {
                throw IllegalArgumentException("Email already exists")
            }
        }
        
        // Create new user with hashed password
        val hashedPassword = passwordEncoder.encode(signupRequest.password)
        val user = User(
            username = signupRequest.username,
            password = hashedPassword,
            email = signupRequest.email,
            fullName = signupRequest.fullName,
            phoneNumber = signupRequest.phoneNumber,
            role = signupRequest.role,
            profileDescription = signupRequest.profileDescription
        )
        
        val createdUser = userService.createUser(user)
        val token = jwtUtil.generateToken(createdUser)
        val refreshToken = generateRefreshToken()
        
        return AuthResponse(
            token = token,
            refreshToken = refreshToken,
            user = mapToUserInfo(createdUser),
            expiresIn = 86400000 // 24 hours
        )
    }
    
    override fun refreshToken(refreshTokenRequest: RefreshTokenRequest): AuthResponse {
        // In a real implementation, you would validate the refresh token
        // For now, we'll just generate a new token
        // TODO: Implement refresh token validation and storage
        
        throw UnsupportedOperationException("Refresh token functionality not yet implemented")
    }
    
    override fun changePassword(userId: Long, changePasswordRequest: ChangePasswordRequest): Boolean {
        val user = userService.getUserById(userId)
            ?: throw IllegalArgumentException("User not found")
        
        // Verify current password
        if (!passwordEncoder.matches(changePasswordRequest.currentPassword, user.password)) {
            throw IllegalArgumentException("Current password is incorrect")
        }
        
        // Hash new password
        val hashedNewPassword = passwordEncoder.encode(changePasswordRequest.newPassword)
        
        // Update user password
        val updatedUser = user.copy(password = hashedNewPassword)
        userService.updateUser(userId, updatedUser)
        
        return true
    }
    
    override fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): Boolean {
        val user = userService.getUserByEmail(forgotPasswordRequest.email)
            ?: return false // Don't reveal if email exists or not
        
        // In a real implementation, you would:
        // 1. Generate a password reset token
        // 2. Store it in the database with expiration
        // 3. Send an email with the reset link
        // 4. Return true to indicate success
        
        // For now, we'll just return true
        return true
    }
    
    override fun resetPassword(resetPasswordRequest: ResetPasswordRequest): Boolean {
        // In a real implementation, you would:
        // 1. Validate the reset token
        // 2. Check if it's expired
        // 3. Find the user associated with the token
        // 4. Update the password
        // 5. Invalidate the token
        
        // For now, we'll throw an exception
        throw UnsupportedOperationException("Password reset functionality not yet implemented")
    }
    
    override fun logout(userId: Long): Boolean {
        // In a real implementation, you would:
        // 1. Add the token to a blacklist
        // 2. Or store logout timestamp in user session
        // 3. Clear any refresh tokens
        
        // For now, we'll just return true
        return true
    }
    
    override fun validateToken(token: String): Boolean {
        return jwtUtil.validateToken(token)
    }
    
    override fun getCurrentUser(token: String): UserInfo? {
        val userInfo = jwtUtil.extractUserInfo(token)
        return userInfo?.let { info ->
            UserInfo(
                id = info.userId,
                username = info.username,
                email = info.email,
                fullName = info.fullName,
                role = info.role,
                isActive = true // We assume if token is valid, user is active
            )
        }
    }
    
    private fun mapToUserInfo(user: User): UserInfo {
        return UserInfo(
            id = user.id,
            username = user.username,
            email = user.email,
            fullName = user.fullName,
            role = user.role,
            isActive = user.isActive
        )
    }
    
    private fun generateRefreshToken(): String {
        return UUID.randomUUID().toString()
    }
} 