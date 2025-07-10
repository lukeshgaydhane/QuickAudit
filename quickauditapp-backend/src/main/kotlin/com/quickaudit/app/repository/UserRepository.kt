package com.quickaudit.app.repository

import com.quickaudit.app.model.User
import com.quickaudit.app.model.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
 
@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
    fun findByRole(role: UserRole): List<User>
    fun findByIsActive(isActive: Boolean): List<User>
} 