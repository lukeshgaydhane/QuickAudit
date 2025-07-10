package com.quickaudit.app.service

import com.quickaudit.app.dto.*
import com.quickaudit.app.model.User
import com.quickaudit.app.model.UserRole
import com.quickaudit.app.security.JwtUtil
import com.quickaudit.app.service.impl.AuthServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.util.ReflectionTestUtils

class AuthServiceTest {
    
    @Mock
    private lateinit var userService: UserService
    
    @Mock
    private lateinit var jwtUtil: JwtUtil
    
    @Mock
    private lateinit var passwordEncoder: PasswordEncoder
    
    private lateinit var authService: AuthService
    
    private lateinit var testUser: User
    
    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authService = AuthServiceImpl(userService, jwtUtil, passwordEncoder)
        
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
        val token = "jwt.token.here"
        
        `when`(userService.authenticateUser("testuser", "password123")).thenReturn(testUser)
        `when`(jwtUtil.generateToken(testUser)).thenReturn(token)
        
        // When
        val result = authService.login(loginRequest)
        
        // Then
        assertNotNull(result)
        assertEquals(token, result.token)
        assertEquals(testUser.id, result.user.id)
        assertEquals(testUser.username, result.user.username)
        assertEquals(testUser.email, result.user.email)
        assertEquals(testUser.role, result.user.role)
        assertTrue(result.user.isActive)
        assertNotNull(result.refreshToken)
        assertEquals(86400000L, result.expiresIn)
        
        verify(userService).authenticateUser("testuser", "password123")
        verify(jwtUtil).generateToken(testUser)
    }
    
    @Test
    fun `test login with invalid credentials throws exception`() {
        // Given
        val loginRequest = LoginRequest("testuser", "wrongpassword")
        
        `when`(userService.authenticateUser("testuser", "wrongpassword")).thenReturn(null)
        
        // When & Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            authService.login(loginRequest)
        }
        
        assertEquals("Invalid username or password", exception.message)
        verify(userService).authenticateUser("testuser", "wrongpassword")
        verify(jwtUtil, never()).generateToken(any())
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
        
        val hashedPassword = "hashedPassword123"
        val token = "jwt.token.here"
        val createdUser = testUser.copy(
            id = 2L,
            username = "newuser",
            password = hashedPassword,
            email = "newuser@example.com",
            fullName = "New User"
        )
        
        `when`(userService.getUserByUsername("newuser")).thenReturn(null)
        `when`(userService.getUserByEmail("newuser@example.com")).thenReturn(null)
        `when`(passwordEncoder.encode("password123")).thenReturn(hashedPassword)
        `when`(userService.createUser(any())).thenReturn(createdUser)
        `when`(jwtUtil.generateToken(createdUser)).thenReturn(token)
        
        // When
        val result = authService.signup(signupRequest)
        
        // Then
        assertNotNull(result)
        assertEquals(token, result.token)
        assertEquals(createdUser.id, result.user.id)
        assertEquals(createdUser.username, result.user.username)
        assertEquals(createdUser.email, result.user.email)
        assertEquals(createdUser.role, result.user.role)
        assertNotNull(result.refreshToken)
        
        verify(userService).getUserByUsername("newuser")
        verify(userService).getUserByEmail("newuser@example.com")
        verify(passwordEncoder).encode("password123")
        verify(userService).createUser(any())
        verify(jwtUtil).generateToken(createdUser)
    }
    
    @Test
    fun `test signup with existing username throws exception`() {
        // Given
        val signupRequest = SignupRequest(
            username = "existinguser",
            password = "password123"
        )
        
        `when`(userService.getUserByUsername("existinguser")).thenReturn(testUser)
        
        // When & Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            authService.signup(signupRequest)
        }
        
        assertEquals("Username already exists", exception.message)
        verify(userService).getUserByUsername("existinguser")
        verify(userService, never()).createUser(any())
    }
    
    @Test
    fun `test signup with existing email throws exception`() {
        // Given
        val signupRequest = SignupRequest(
            username = "newuser",
            password = "password123",
            email = "existing@example.com"
        )
        
        `when`(userService.getUserByUsername("newuser")).thenReturn(null)
        `when`(userService.getUserByEmail("existing@example.com")).thenReturn(testUser)
        
        // When & Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            authService.signup(signupRequest)
        }
        
        assertEquals("Email already exists", exception.message)
        verify(userService).getUserByUsername("newuser")
        verify(userService).getUserByEmail("existing@example.com")
        verify(userService, never()).createUser(any())
    }
    
    @Test
    fun `test successful change password`() {
        // Given
        val changePasswordRequest = ChangePasswordRequest(
            currentPassword = "oldpassword",
            newPassword = "newpassword123"
        )
        val hashedNewPassword = "hashedNewPassword123"
        
        `when`(userService.getUserById(1L)).thenReturn(testUser)
        `when`(passwordEncoder.matches("oldpassword", "hashedPassword123")).thenReturn(true)
        `when`(passwordEncoder.encode("newpassword123")).thenReturn(hashedNewPassword)
        `when`(userService.updateUser(1L, any())).thenReturn(testUser.copy(password = hashedNewPassword))
        
        // When
        val result = authService.changePassword(1L, changePasswordRequest)
        
        // Then
        assertTrue(result)
        verify(userService).getUserById(1L)
        verify(passwordEncoder).matches("oldpassword", "hashedPassword123")
        verify(passwordEncoder).encode("newpassword123")
        verify(userService).updateUser(1L, any())
    }
    
    @Test
    fun `test change password with wrong current password throws exception`() {
        // Given
        val changePasswordRequest = ChangePasswordRequest(
            currentPassword = "wrongpassword",
            newPassword = "newpassword123"
        )
        
        `when`(userService.getUserById(1L)).thenReturn(testUser)
        `when`(passwordEncoder.matches("wrongpassword", "hashedPassword123")).thenReturn(false)
        
        // When & Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            authService.changePassword(1L, changePasswordRequest)
        }
        
        assertEquals("Current password is incorrect", exception.message)
        verify(userService).getUserById(1L)
        verify(passwordEncoder).matches("wrongpassword", "hashedPassword123")
        verify(passwordEncoder, never()).encode(any())
        verify(userService, never()).updateUser(any(), any())
    }
    
    @Test
    fun `test change password with non-existent user throws exception`() {
        // Given
        val changePasswordRequest = ChangePasswordRequest(
            currentPassword = "oldpassword",
            newPassword = "newpassword123"
        )
        
        `when`(userService.getUserById(999L)).thenReturn(null)
        
        // When & Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            authService.changePassword(999L, changePasswordRequest)
        }
        
        assertEquals("User not found", exception.message)
        verify(userService).getUserById(999L)
        verify(passwordEncoder, never()).matches(any(), any())
    }
    
    @Test
    fun `test forgot password with existing email returns true`() {
        // Given
        val forgotPasswordRequest = ForgotPasswordRequest("test@example.com")
        
        `when`(userService.getUserByEmail("test@example.com")).thenReturn(testUser)
        
        // When
        val result = authService.forgotPassword(forgotPasswordRequest)
        
        // Then
        assertTrue(result)
        verify(userService).getUserByEmail("test@example.com")
    }
    
    @Test
    fun `test forgot password with non-existing email returns false`() {
        // Given
        val forgotPasswordRequest = ForgotPasswordRequest("nonexistent@example.com")
        
        `when`(userService.getUserByEmail("nonexistent@example.com")).thenReturn(null)
        
        // When
        val result = authService.forgotPassword(forgotPasswordRequest)
        
        // Then
        assertFalse(result)
        verify(userService).getUserByEmail("nonexistent@example.com")
    }
    
    @Test
    fun `test validate token with valid token returns true`() {
        // Given
        val token = "valid.token.here"
        
        `when`(jwtUtil.validateToken(token)).thenReturn(true)
        
        // When
        val result = authService.validateToken(token)
        
        // Then
        assertTrue(result)
        verify(jwtUtil).validateToken(token)
    }
    
    @Test
    fun `test validate token with invalid token returns false`() {
        // Given
        val token = "invalid.token.here"
        
        `when`(jwtUtil.validateToken(token)).thenReturn(false)
        
        // When
        val result = authService.validateToken(token)
        
        // Then
        assertFalse(result)
        verify(jwtUtil).validateToken(token)
    }
    
    @Test
    fun `test get current user with valid token returns user info`() {
        // Given
        val token = "valid.token.here"
        val userInfo = com.quickaudit.app.security.UserInfo(
            userId = 1L,
            username = "testuser",
            email = "test@example.com",
            role = UserRole.USER,
            fullName = "Test User"
        )
        
        `when`(jwtUtil.extractUserInfo(token)).thenReturn(userInfo)
        
        // When
        val result = authService.getCurrentUser(token)
        
        // Then
        assertNotNull(result)
        assertEquals(userInfo.userId, result?.id)
        assertEquals(userInfo.username, result?.username)
        assertEquals(userInfo.email, result?.email)
        assertEquals(userInfo.role, result?.role)
        assertEquals(userInfo.fullName, result?.fullName)
        assertTrue(result?.isActive == true)
        
        verify(jwtUtil).extractUserInfo(token)
    }
    
    @Test
    fun `test get current user with invalid token returns null`() {
        // Given
        val token = "invalid.token.here"
        
        `when`(jwtUtil.extractUserInfo(token)).thenReturn(null)
        
        // When
        val result = authService.getCurrentUser(token)
        
        // Then
        assertNull(result)
        verify(jwtUtil).extractUserInfo(token)
    }
    
    @Test
    fun `test refresh token throws unsupported operation exception`() {
        // Given
        val refreshTokenRequest = RefreshTokenRequest("refresh.token.here")
        
        // When & Then
        val exception = assertThrows(UnsupportedOperationException::class.java) {
            authService.refreshToken(refreshTokenRequest)
        }
        
        assertEquals("Refresh token functionality not yet implemented", exception.message)
    }
    
    @Test
    fun `test reset password throws unsupported operation exception`() {
        // Given
        val resetPasswordRequest = ResetPasswordRequest("reset.token.here", "newpassword123")
        
        // When & Then
        val exception = assertThrows(UnsupportedOperationException::class.java) {
            authService.resetPassword(resetPasswordRequest)
        }
        
        assertEquals("Password reset functionality not yet implemented", exception.message)
    }
    
    @Test
    fun `test logout returns true`() {
        // When
        val result = authService.logout(1L)
        
        // Then
        assertTrue(result)
    }
} 