package com.quickaudit.app.service.impl

import com.quickaudit.app.model.User
import com.quickaudit.app.model.UserRole
import com.quickaudit.app.repository.UserRepository
import com.quickaudit.app.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {
    
    override fun getAllUsers(): List<User> = userRepository.findAll()
    
    override fun getUserById(id: Long): User? = userRepository.findById(id).orElse(null)
    
    override fun getUserByUsername(username: String): User? = userRepository.findByUsername(username)
    
    override fun getUserByEmail(email: String): User? = userRepository.findByEmail(email)
    
    override fun createUser(user: User): User {
        // Validate unique constraints
        userRepository.findByUsername(user.username)?.let {
            throw IllegalArgumentException("Username already exists")
        }
        user.email?.let { email ->
            userRepository.findByEmail(email)?.let {
                throw IllegalArgumentException("Email already exists")
            }
        }
        return userRepository.save(user)
    }
    
    override fun updateUser(id: Long, user: User): User? {
        val existingUser = getUserById(id) ?: return null
        
        // Check if username is being changed and if it's already taken
        if (user.username != existingUser.username) {
            userRepository.findByUsername(user.username)?.let {
                throw IllegalArgumentException("Username already exists")
            }
        }
        
        // Check if email is being changed and if it's already taken
        if (user.email != existingUser.email && user.email != null) {
            userRepository.findByEmail(user.email)?.let {
                throw IllegalArgumentException("Email already exists")
            }
        }
        
        val updatedUser = existingUser.copy(
            username = user.username,
            password = user.password,
            email = user.email,
            fullName = user.fullName,
            phoneNumber = user.phoneNumber,
            role = user.role,
            profileDescription = user.profileDescription,
            profileImageUrl = user.profileImageUrl
        )
        
        return userRepository.save(updatedUser)
    }
    
    override fun deleteUser(id: Long): Boolean {
        val user = getUserById(id) ?: return false
        userRepository.delete(user)
        return true
    }
    
    override fun authenticateUser(username: String, password: String): User? {
        val user = getUserByUsername(username) ?: return null
        return if (passwordEncoder.matches(password, user.password) && user.isActive) {
            updateLastLogin(user.id)
        } else null
    }
    
    override fun updateLastLogin(userId: Long): User? {
        val user = getUserById(userId) ?: return null
        val updatedUser = user.copy(lastLoginAt = LocalDateTime.now())
        return userRepository.save(updatedUser)
    }
    
    override fun getUsersByRole(role: UserRole): List<User> = userRepository.findByRole(role)
    
    override fun activateUser(userId: Long): User? {
        val user = getUserById(userId) ?: return null
        val updatedUser = user.copy(isActive = true)
        return userRepository.save(updatedUser)
    }
    
    override fun deactivateUser(userId: Long): User? {
        val user = getUserById(userId) ?: return null
        val updatedUser = user.copy(isActive = false)
        return userRepository.save(updatedUser)
    }
} 