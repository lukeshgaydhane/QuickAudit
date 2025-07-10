package com.quickaudit.app.security

import com.quickaudit.app.model.User
import com.quickaudit.app.model.UserRole
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDateTime

class JwtUtilTest {
    
    private lateinit var jwtUtil: JwtUtil
    private lateinit var testUser: User
    
    @BeforeEach
    fun setUp() {
        jwtUtil = JwtUtil()
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKeyForJwtTokenGenerationAndValidation")
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L)
        
        testUser = User(
            id = 1L,
            username = "testuser",
            password = "password123",
            email = "test@example.com",
            fullName = "Test User",
            role = UserRole.ADMIN
        )
    }
    
    @Test
    fun `test generate token for user`() {
        val token = jwtUtil.generateToken(testUser)
        
        assertNotNull(token)
        assertTrue(token.isNotEmpty())
        assertTrue(token.contains(".")) // JWT format: header.payload.signature
    }
    
    @Test
    fun `test extract username from token`() {
        val token = jwtUtil.generateToken(testUser)
        val extractedUsername = jwtUtil.extractUsername(token)
        
        assertEquals(testUser.username, extractedUsername)
    }
    
    @Test
    fun `test extract user id from token`() {
        val token = jwtUtil.generateToken(testUser)
        val extractedUserId = jwtUtil.extractUserId(token)
        
        assertEquals(testUser.id, extractedUserId)
    }
    
    @Test
    fun `test extract user role from token`() {
        val token = jwtUtil.generateToken(testUser)
        val extractedRole = jwtUtil.extractUserRole(token)
        
        assertEquals(testUser.role, extractedRole)
    }
    
    @Test
    fun `test extract user info from token`() {
        val token = jwtUtil.generateToken(testUser)
        val userInfo = jwtUtil.extractUserInfo(token)
        
        assertNotNull(userInfo)
        assertEquals(testUser.id, userInfo?.userId)
        assertEquals(testUser.username, userInfo?.username)
        assertEquals(testUser.email, userInfo?.email)
        assertEquals(testUser.fullName, userInfo?.fullName)
        assertEquals(testUser.role, userInfo?.role)
    }
    
    @Test
    fun `test validate token with valid token`() {
        val token = jwtUtil.generateToken(testUser)
        val isValid = jwtUtil.validateToken(token)
        
        assertTrue(isValid)
    }
    
    @Test
    fun `test validate token with invalid token`() {
        val invalidToken = "invalid.token.here"
        val isValid = jwtUtil.validateToken(invalidToken)
        
        assertFalse(isValid)
    }
    
    @Test
    fun `test validate token with expired token`() {
        // Create JWT util with very short expiration
        val shortExpirationJwtUtil = JwtUtil()
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "secret", "testSecretKey")
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "expiration", 1L) // 1ms expiration
        
        val token = shortExpirationJwtUtil.generateToken(testUser)
        
        // Wait for token to expire
        Thread.sleep(10)
        
        val isValid = shortExpirationJwtUtil.validateToken(token)
        assertFalse(isValid)
    }
    
    @Test
    fun `test extract username from invalid token returns null`() {
        val invalidToken = "invalid.token.here"
        val extractedUsername = jwtUtil.extractUsername(invalidToken)
        
        assertNull(extractedUsername)
    }
    
    @Test
    fun `test extract user id from invalid token returns null`() {
        val invalidToken = "invalid.token.here"
        val extractedUserId = jwtUtil.extractUserId(invalidToken)
        
        assertNull(extractedUserId)
    }
    
    @Test
    fun `test extract user role from invalid token returns null`() {
        val invalidToken = "invalid.token.here"
        val extractedRole = jwtUtil.extractUserRole(invalidToken)
        
        assertNull(extractedRole)
    }
    
    @Test
    fun `test extract user info from invalid token returns null`() {
        val invalidToken = "invalid.token.here"
        val userInfo = jwtUtil.extractUserInfo(invalidToken)
        
        assertNull(userInfo)
    }
    
    @Test
    fun `test token contains all user information`() {
        val userWithAllFields = User(
            id = 2L,
            username = "completeuser",
            password = "password123",
            email = "complete@example.com",
            fullName = "Complete User",
            phoneNumber = "+1234567890",
            role = UserRole.MANAGER,
            isActive = true,
            profileDescription = "A complete user profile",
            profileImageUrl = "https://example.com/avatar.jpg"
        )
        
        val token = jwtUtil.generateToken(userWithAllFields)
        val userInfo = jwtUtil.extractUserInfo(token)
        
        assertNotNull(userInfo)
        assertEquals(userWithAllFields.id, userInfo?.userId)
        assertEquals(userWithAllFields.username, userInfo?.username)
        assertEquals(userWithAllFields.email, userInfo?.email)
        assertEquals(userWithAllFields.fullName, userInfo?.fullName)
        assertEquals(userWithAllFields.role, userInfo?.role)
    }
    
    @Test
    fun `test different users generate different tokens`() {
        val user1 = User(
            id = 1L,
            username = "user1",
            password = "password123",
            role = UserRole.USER
        )
        
        val user2 = User(
            id = 2L,
            username = "user2",
            password = "password123",
            role = UserRole.ADMIN
        )
        
        val token1 = jwtUtil.generateToken(user1)
        val token2 = jwtUtil.generateToken(user2)
        
        assertNotEquals(token1, token2)
        
        val userInfo1 = jwtUtil.extractUserInfo(token1)
        val userInfo2 = jwtUtil.extractUserInfo(token2)
        
        assertEquals(user1.id, userInfo1?.userId)
        assertEquals(user2.id, userInfo2?.userId)
        assertEquals(user1.username, userInfo1?.username)
        assertEquals(user2.username, userInfo2?.username)
    }
    
    @Test
    fun `test token expiration time`() {
        val token = jwtUtil.generateToken(testUser)
        val expiration = jwtUtil.extractExpiration(token)
        
        assertNotNull(expiration)
        assertTrue(expiration!!.after(java.util.Date()))
    }
} 