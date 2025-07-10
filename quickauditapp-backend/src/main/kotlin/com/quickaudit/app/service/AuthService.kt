package com.quickaudit.app.service

import com.quickaudit.app.dto.*

interface AuthService {
    fun login(loginRequest: LoginRequest): AuthResponse
    fun signup(signupRequest: SignupRequest): AuthResponse
    fun refreshToken(refreshTokenRequest: RefreshTokenRequest): AuthResponse
    fun changePassword(userId: Long, changePasswordRequest: ChangePasswordRequest): Boolean
    fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): Boolean
    fun resetPassword(resetPasswordRequest: ResetPasswordRequest): Boolean
    fun logout(userId: Long): Boolean
    fun validateToken(token: String): Boolean
    fun getCurrentUser(token: String): UserInfo?
} 