package com.quickaudit.app.controller

import com.quickaudit.app.model.User
import com.quickaudit.app.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userRepository: UserRepository) {
    @GetMapping
    fun getAllUsers(): List<User> = userRepository.findAll()

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<User> =
        userRepository.findById(id).map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())

    @PostMapping
    fun createUser(@RequestBody user: User): User = userRepository.save(user)

    @PostMapping("/login")
    fun login(@RequestBody login: Map<String, String>): ResponseEntity<User> {
        val user = userRepository.findByUsername(login["username"] ?: "")
        return if (user != null && user.password == login["password"]) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.status(401).build()
        }
    }
} 