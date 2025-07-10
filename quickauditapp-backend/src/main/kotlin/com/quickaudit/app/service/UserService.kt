package com.quickaudit.app.service

import com.quickaudit.app.model.User
import com.quickaudit.app.model.UserRole

interface UserService {
    fun getAllUsers(): List<User>
    fun getUserById(id: Long): User?
    fun getUserByUsername(username: String): User?
    fun getUserByEmail(email: String): User?
    fun createUser(user: User): User
    fun updateUser(id: Long, user: User): User?
    fun deleteUser(id: Long): Boolean
    fun authenticateUser(username: String, password: String): User?
    fun updateLastLogin(userId: Long): User?
    fun getUsersByRole(role: UserRole): List<User>
    fun activateUser(userId: Long): User?
    fun deactivateUser(userId: Long): User?
} 