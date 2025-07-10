package com.quickaudit.app.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.quickaudit.app.dto.*
import com.quickaudit.app.model.User
import com.quickaudit.app.model.UserRole
import com.quickaudit.app.security.JwtUtil
import com.quickaudit.app.service.AuthService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(AuthController::class)
class AuthControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @MockBean
    private lateinit var authService: AuthService
    
    @MockBean
    private lateinit var jwtUtil: JwtUtil
    
    private lateinit var objectMapper: ObjectMapper
    
    private lateinit var testUser: User
    
    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper()
        testUser = User(
            id = 1L,
            username = "testuser",
            password = "hashedPassword123",
            email = "test@example.com",
            fullName = "Test User",
            role = UserRole.USER,
            isActive = true
        )
    }
    
    @Test
    fun `test successful login`() {
        // Given
        val loginRequest = LoginRequest("testuser", "password123")
        val authResponse = AuthResponse(
            token = "jwt.token.here",
            refreshToken = "refresh.token.here",
            user = UserInfo(
                id = 1L,
                username = "testuser",
                email = "test@example.com",
                fullName = "Test User",
                role = UserRole.USER,
                isActive = true
            ),
            expiresIn = 86400000L
        )
        
        `when`(authService.login(loginRequest)).thenReturn(authResponse)
        
        // When & Then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Login successful"))
            .andExpect(jsonPath("$.data.token").value("jwt.token.here"))
            .andExpect(jsonPath("$.data.user.username").value("testuser"))
        
        verify(authService).login(loginRequest)
    }
    
    @Test
    fun `test login with invalid credentials returns bad request`() {
        // Given
        val loginRequest = LoginRequest("testuser", "wrongpassword")
        
        `when`(authService.login(loginRequest)).thenThrow(IllegalArgumentException("Invalid username or password"))
        
        // When & Then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Invalid username or password"))
        
        verify(authService).login(loginRequest)
    }
    
    @Test
    fun `test successful signup`() {
        // Given
        val signupRequest = SignupRequest(
            username = "newuser",
            password = "password123",
            email = "newuser@example.com",
            fullName = "New User",
            role = UserRole.USER
        )
        val authResponse = AuthResponse(
            token = "jwt.token.here",
            refreshToken = "refresh.token.here",
            user = UserInfo(
                id = 2L,
                username = "newuser",
                email = "newuser@example.com",
                fullName = "New User",
                role = UserRole.USER,
                isActive = true
            ),
            expiresIn = 86400000L
        )
        
        `when`(authService.signup(signupRequest)).thenReturn(authResponse)
        
        // When & Then
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("User registered successfully"))
            .andExpect(jsonPath("$.data.user.username").value("newuser"))
        
        verify(authService).signup(signupRequest)
    }
    
    @Test
    fun `test signup with existing username returns bad request`() {
        // Given
        val signupRequest = SignupRequest(
            username = "existinguser",
            password = "password123"
        )
        
        `when`(authService.signup(signupRequest)).thenThrow(IllegalArgumentException("Username already exists"))
        
        // When & Then
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Username already exists"))
        
        verify(authService).signup(signupRequest)
    }
    
    @Test
    fun `test logout returns success`() {
        // Given
        val token = "jwt.token.here"
        `when`(jwtUtil.extractUserId(token)).thenReturn(1L)
        
        // When & Then
        mockMvc.perform(
            post("/api/auth/logout")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Logout successful"))
        
        verify(jwtUtil).extractUserId(token)
        verify(authService).logout(1L)
    }
    
    @Test
    fun `test get current user with valid token returns user info`() {
        // Given
        val token = "jwt.token.here"
        val userInfo = UserInfo(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            fullName = "Test User",
            role = UserRole.USER,
            isActive = true
        )
        
        `when`(authService.getCurrentUser(token)).thenReturn(userInfo)
        
        // When & Then
        mockMvc.perform(
            get("/api/auth/me")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("User information retrieved successfully"))
            .andExpect(jsonPath("$.data.username").value("testuser"))
        
        verify(authService).getCurrentUser(token)
    }
    
    @Test
    fun `test get current user without token returns bad request`() {
        // When & Then
        mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Authentication required"))
    }
    
    @Test
    fun `test validate token with valid token returns success`() {
        // Given
        val token = "jwt.token.here"
        `when`(authService.validateToken(token)).thenReturn(true)
        
        // When & Then
        mockMvc.perform(
            get("/api/auth/validate")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Token is valid"))
        
        verify(authService).validateToken(token)
    }
    
    @Test
    fun `test validate token with invalid token returns bad request`() {
        // Given
        val token = "invalid.token.here"
        `when`(authService.validateToken(token)).thenReturn(false)
        
        // When & Then
        mockMvc.perform(
            get("/api/auth/validate")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Token is invalid or expired"))
        
        verify(authService).validateToken(token)
    }
    
    @Test
    fun `test change password with valid request returns success`() {
        // Given
        val token = "jwt.token.here"
        val changePasswordRequest = ChangePasswordRequest(
            currentPassword = "oldpassword",
            newPassword = "newpassword123"
        )
        
        `when`(jwtUtil.extractUserId(token)).thenReturn(1L)
        `when`(authService.changePassword(1L, changePasswordRequest)).thenReturn(true)
        
        // When & Then
        mockMvc.perform(
            post("/api/auth/change-password")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Password changed successfully"))
        
        verify(jwtUtil).extractUserId(token)
        verify(authService).changePassword(1L, changePasswordRequest)
    }
    
    @Test
    fun `test forgot password with valid email returns success`() {
        // Given
        val forgotPasswordRequest = ForgotPasswordRequest("test@example.com")
        
        `when`(authService.forgotPassword(forgotPasswordRequest)).thenReturn(true)
        
        // When & Then
        mockMvc.perform(
            post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Password reset instructions sent to your email"))
        
        verify(authService).forgotPassword(forgotPasswordRequest)
    }
} 