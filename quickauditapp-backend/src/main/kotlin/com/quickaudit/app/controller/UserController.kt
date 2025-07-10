package com.quickaudit.app.controller

import com.quickaudit.app.model.User
import com.quickaudit.app.model.UserRole
import com.quickaudit.app.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    
    @GetMapping
    fun getAllUsers(): List<User> = userService.getAllUsers()
    
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<User> =
        userService.getUserById(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    
    @PostMapping
    fun createUser(@Valid @RequestBody user: User): ResponseEntity<User> {
        return try {
            val createdUser = userService.createUser(user)
            ResponseEntity.ok(createdUser)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @Valid @RequestBody user: User): ResponseEntity<User> {
        return try {
            val updatedUser = userService.updateUser(id, user)
            updatedUser?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        return if (userService.deleteUser(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @PostMapping("/login")
    fun login(@RequestBody login: Map<String, String>): ResponseEntity<User> {
        val username = login["username"] ?: return ResponseEntity.badRequest().build()
        val password = login["password"] ?: return ResponseEntity.badRequest().build()
        
        val user = userService.authenticateUser(username, password)
        return user?.let { ResponseEntity.ok(it) } ?: ResponseEntity.status(401).build()
    }
    
    @GetMapping("/role/{role}")
    fun getUsersByRole(@PathVariable role: UserRole): List<User> = userService.getUsersByRole(role)
    
    @PostMapping("/{id}/activate")
    fun activateUser(@PathVariable id: Long): ResponseEntity<User> =
        userService.activateUser(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    
    @PostMapping("/{id}/deactivate")
    fun deactivateUser(@PathVariable id: Long): ResponseEntity<User> =
        userService.deactivateUser(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
} 