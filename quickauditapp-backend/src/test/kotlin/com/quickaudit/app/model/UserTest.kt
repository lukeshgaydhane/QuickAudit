package com.quickaudit.app.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import java.time.LocalDateTime

class UserTest {
    
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator
    
    @Test
    fun `test valid user creation`() {
        val user = User(
            username = "testuser",
            password = "password123",
            email = "test@example.com",
            fullName = "Test User",
            role = UserRole.USER
        )
        
        val violations = validator.validate(user)
        assertTrue(violations.isEmpty(), "User should be valid")
        assertEquals("testuser", user.username)
        assertEquals("password123", user.password)
        assertEquals("test@example.com", user.email)
        assertEquals("Test User", user.fullName)
        assertEquals(UserRole.USER, user.role)
        assertTrue(user.isActive)
        assertNotNull(user.createdAt)
    }
    
    @Test
    fun `test user with minimum required fields`() {
        val user = User(
            username = "minuser",
            password = "pass123"
        )
        
        val violations = validator.validate(user)
        assertTrue(violations.isEmpty(), "User with minimum fields should be valid")
        assertEquals("minuser", user.username)
        assertEquals("pass123", user.password)
        assertNull(user.email)
        assertNull(user.fullName)
        assertEquals(UserRole.USER, user.role)
    }
    
    @Test
    fun `test invalid username - too short`() {
        val user = User(
            username = "ab", // too short
            password = "password123"
        )
        
        val violations = validator.validate(user)
        assertFalse(violations.isEmpty(), "User with short username should be invalid")
        assertTrue(violations.any { it.propertyPath.toString() == "username" })
    }
    
    @Test
    fun `test invalid username - too long`() {
        val user = User(
            username = "a".repeat(51), // too long
            password = "password123"
        )
        
        val violations = validator.validate(user)
        assertFalse(violations.isEmpty(), "User with long username should be invalid")
        assertTrue(violations.any { it.propertyPath.toString() == "username" })
    }
    
    @Test
    fun `test invalid password - too short`() {
        val user = User(
            username = "testuser",
            password = "123" // too short
        )
        
        val violations = validator.validate(user)
        assertFalse(violations.isEmpty(), "User with short password should be invalid")
        assertTrue(violations.any { it.propertyPath.toString() == "password" })
    }
    
    @Test
    fun `test invalid email format`() {
        val user = User(
            username = "testuser",
            password = "password123",
            email = "invalid-email"
        )
        
        val violations = validator.validate(user)
        assertFalse(violations.isEmpty(), "User with invalid email should be invalid")
        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }
    
    @Test
    fun `test valid email formats`() {
        val validEmails = listOf(
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org"
        )
        
        validEmails.forEach { email ->
            val user = User(
                username = "testuser",
                password = "password123",
                email = email
            )
            
            val violations = validator.validate(user)
            assertTrue(violations.isEmpty(), "User with email $email should be valid")
        }
    }
    
    @Test
    fun `test user role assignment`() {
        val roles = UserRole.values()
        
        roles.forEach { role ->
            val user = User(
                username = "user_${role.name.lowercase()}",
                password = "password123",
                role = role
            )
            
            assertEquals(role, user.role)
        }
    }
    
    @Test
    fun `test user copy with modifications`() {
        val originalUser = User(
            username = "original",
            password = "password123",
            email = "original@example.com",
            fullName = "Original User"
        )
        
        val modifiedUser = originalUser.copy(
            username = "modified",
            email = "modified@example.com",
            fullName = "Modified User"
        )
        
        assertEquals("modified", modifiedUser.username)
        assertEquals("modified@example.com", modifiedUser.email)
        assertEquals("Modified User", modifiedUser.fullName)
        assertEquals("password123", modifiedUser.password) // unchanged
        assertEquals(originalUser.id, modifiedUser.id) // unchanged
    }
    
    @Test
    fun `test user with all optional fields`() {
        val user = User(
            username = "completeuser",
            password = "password123",
            email = "complete@example.com",
            fullName = "Complete User",
            phoneNumber = "+1234567890",
            role = UserRole.ADMIN,
            isActive = false,
            profileDescription = "A complete user profile with all fields filled",
            profileImageUrl = "https://example.com/avatar.jpg"
        )
        
        val violations = validator.validate(user)
        assertTrue(violations.isEmpty(), "Complete user should be valid")
        assertEquals("completeuser", user.username)
        assertEquals("complete@example.com", user.email)
        assertEquals("Complete User", user.fullName)
        assertEquals("+1234567890", user.phoneNumber)
        assertEquals(UserRole.ADMIN, user.role)
        assertFalse(user.isActive)
        assertEquals("A complete user profile with all fields filled", user.profileDescription)
        assertEquals("https://example.com/avatar.jpg", user.profileImageUrl)
    }
} 