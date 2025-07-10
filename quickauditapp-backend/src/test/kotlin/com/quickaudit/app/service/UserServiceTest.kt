package com.quickaudit.app.service

import com.quickaudit.app.model.User
import com.quickaudit.app.model.UserRole
import com.quickaudit.app.repository.UserRepository
import com.quickaudit.app.service.impl.UserServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime
import java.util.*

class UserServiceTest {
    
    @Mock
    private lateinit var userRepository: UserRepository
    
    private lateinit var userService: UserService
    
    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userService = UserServiceImpl(userRepository)
    }
    
    @Test
    fun `test create user successfully`() {
        // Given
        val user = User(
            username = "newuser",
            password = "password123",
            email = "newuser@example.com",
            fullName = "New User"
        )
        
        `when`(userRepository.findByUsername("newuser")).thenReturn(null)
        `when`(userRepository.findByEmail("newuser@example.com")).thenReturn(null)
        `when`(userRepository.save(user)).thenReturn(user.copy(id = 1L))
        
        // When
        val createdUser = userService.createUser(user)
        
        // Then
        assertNotNull(createdUser)
        assertEquals(1L, createdUser.id)
        assertEquals("newuser", createdUser.username)
        verify(userRepository).findByUsername("newuser")
        verify(userRepository).findByEmail("newuser@example.com")
        verify(userRepository).save(user)
    }
    
    @Test
    fun `test create user with duplicate username throws exception`() {
        // Given
        val existingUser = User(
            id = 1L,
            username = "existinguser",
            password = "password123"
        )
        
        val newUser = User(
            username = "existinguser",
            password = "password123"
        )
        
        `when`(userRepository.findByUsername("existinguser")).thenReturn(existingUser)
        
        // When & Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.createUser(newUser)
        }
        
        assertEquals("Username already exists", exception.message)
        verify(userRepository).findByUsername("existinguser")
        verify(userRepository, never()).save(any())
    }
    
    @Test
    fun `test create user with duplicate email throws exception`() {
        // Given
        val existingUser = User(
            id = 1L,
            username = "existinguser",
            password = "password123",
            email = "existing@example.com"
        )
        
        val newUser = User(
            username = "newuser",
            password = "password123",
            email = "existing@example.com"
        )
        
        `when`(userRepository.findByUsername("newuser")).thenReturn(null)
        `when`(userRepository.findByEmail("existing@example.com")).thenReturn(existingUser)
        
        // When & Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.createUser(newUser)
        }
        
        assertEquals("Email already exists", exception.message)
        verify(userRepository).findByUsername("newuser")
        verify(userRepository).findByEmail("existing@example.com")
        verify(userRepository, never()).save(any())
    }
    
    @Test
    fun `test get user by id successfully`() {
        // Given
        val user = User(
            id = 1L,
            username = "testuser",
            password = "password123"
        )
        
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
        
        // When
        val foundUser = userService.getUserById(1L)
        
        // Then
        assertNotNull(foundUser)
        assertEquals(1L, foundUser?.id)
        assertEquals("testuser", foundUser?.username)
        verify(userRepository).findById(1L)
    }
    
    @Test
    fun `test get user by id not found`() {
        // Given
        `when`(userRepository.findById(999L)).thenReturn(Optional.empty())
        
        // When
        val foundUser = userService.getUserById(999L)
        
        // Then
        assertNull(foundUser)
        verify(userRepository).findById(999L)
    }
    
    @Test
    fun `test get user by username successfully`() {
        // Given
        val user = User(
            id = 1L,
            username = "testuser",
            password = "password123"
        )
        
        `when`(userRepository.findByUsername("testuser")).thenReturn(user)
        
        // When
        val foundUser = userService.getUserByUsername("testuser")
        
        // Then
        assertNotNull(foundUser)
        assertEquals("testuser", foundUser?.username)
        verify(userRepository).findByUsername("testuser")
    }
    
    @Test
    fun `test authenticate user successfully`() {
        // Given
        val user = User(
            id = 1L,
            username = "testuser",
            password = "password123",
            isActive = true
        )
        
        `when`(userRepository.findByUsername("testuser")).thenReturn(user)
        `when`(userRepository.save(any())).thenReturn(user.copy(lastLoginAt = LocalDateTime.now()))
        
        // When
        val authenticatedUser = userService.authenticateUser("testuser", "password123")
        
        // Then
        assertNotNull(authenticatedUser)
        assertEquals("testuser", authenticatedUser?.username)
        verify(userRepository).findByUsername("testuser")
        verify(userRepository).save(any())
    }
    
    @Test
    fun `test authenticate user with wrong password returns null`() {
        // Given
        val user = User(
            id = 1L,
            username = "testuser",
            password = "password123",
            isActive = true
        )
        
        `when`(userRepository.findByUsername("testuser")).thenReturn(user)
        
        // When
        val authenticatedUser = userService.authenticateUser("testuser", "wrongpassword")
        
        // Then
        assertNull(authenticatedUser)
        verify(userRepository).findByUsername("testuser")
        verify(userRepository, never()).save(any())
    }
    
    @Test
    fun `test authenticate inactive user returns null`() {
        // Given
        val user = User(
            id = 1L,
            username = "testuser",
            password = "password123",
            isActive = false
        )
        
        `when`(userRepository.findByUsername("testuser")).thenReturn(user)
        
        // When
        val authenticatedUser = userService.authenticateUser("testuser", "password123")
        
        // Then
        assertNull(authenticatedUser)
        verify(userRepository).findByUsername("testuser")
        verify(userRepository, never()).save(any())
    }
    
    @Test
    fun `test update user successfully`() {
        // Given
        val existingUser = User(
            id = 1L,
            username = "olduser",
            password = "oldpassword",
            email = "old@example.com"
        )
        
        val updatedUser = User(
            username = "newuser",
            password = "newpassword",
            email = "new@example.com",
            fullName = "Updated User"
        )
        
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(existingUser))
        `when`(userRepository.findByUsername("newuser")).thenReturn(null)
        `when`(userRepository.findByEmail("new@example.com")).thenReturn(null)
        `when`(userRepository.save(any())).thenReturn(existingUser.copy(
            username = "newuser",
            password = "newpassword",
            email = "new@example.com",
            fullName = "Updated User"
        ))
        
        // When
        val result = userService.updateUser(1L, updatedUser)
        
        // Then
        assertNotNull(result)
        assertEquals("newuser", result?.username)
        assertEquals("new@example.com", result?.email)
        assertEquals("Updated User", result?.fullName)
        verify(userRepository).findById(1L)
        verify(userRepository).findByUsername("newuser")
        verify(userRepository).findByEmail("new@example.com")
        verify(userRepository).save(any())
    }
    
    @Test
    fun `test delete user successfully`() {
        // Given
        val user = User(
            id = 1L,
            username = "testuser",
            password = "password123"
        )
        
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
        
        // When
        val result = userService.deleteUser(1L)
        
        // Then
        assertTrue(result)
        verify(userRepository).findById(1L)
        verify(userRepository).delete(user)
    }
    
    @Test
    fun `test delete user not found returns false`() {
        // Given
        `when`(userRepository.findById(999L)).thenReturn(Optional.empty())
        
        // When
        val result = userService.deleteUser(999L)
        
        // Then
        assertFalse(result)
        verify(userRepository).findById(999L)
        verify(userRepository, never()).delete(any())
    }
    
    @Test
    fun `test get users by role`() {
        // Given
        val adminUsers = listOf(
            User(id = 1L, username = "admin1", password = "pass", role = UserRole.ADMIN),
            User(id = 2L, username = "admin2", password = "pass", role = UserRole.ADMIN)
        )
        
        `when`(userRepository.findByRole(UserRole.ADMIN)).thenReturn(adminUsers)
        
        // When
        val result = userService.getUsersByRole(UserRole.ADMIN)
        
        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.role == UserRole.ADMIN })
        verify(userRepository).findByRole(UserRole.ADMIN)
    }
    
    @Test
    fun `test activate user`() {
        // Given
        val inactiveUser = User(
            id = 1L,
            username = "testuser",
            password = "password123",
            isActive = false
        )
        
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(inactiveUser))
        `when`(userRepository.save(any())).thenReturn(inactiveUser.copy(isActive = true))
        
        // When
        val result = userService.activateUser(1L)
        
        // Then
        assertNotNull(result)
        assertTrue(result?.isActive == true)
        verify(userRepository).findById(1L)
        verify(userRepository).save(any())
    }
    
    @Test
    fun `test deactivate user`() {
        // Given
        val activeUser = User(
            id = 1L,
            username = "testuser",
            password = "password123",
            isActive = true
        )
        
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(activeUser))
        `when`(userRepository.save(any())).thenReturn(activeUser.copy(isActive = false))
        
        // When
        val result = userService.deactivateUser(1L)
        
        // Then
        assertNotNull(result)
        assertFalse(result?.isActive == true)
        verify(userRepository).findById(1L)
        verify(userRepository).save(any())
    }
} 