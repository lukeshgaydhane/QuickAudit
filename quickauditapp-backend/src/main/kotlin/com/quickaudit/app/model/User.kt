package com.quickaudit.app.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val username: String,
    val password: String,
    val email: String? = null,
    val fullName: String? = null
) 