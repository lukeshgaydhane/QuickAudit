package com.quickaudit.app.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "audits")
data class Audit(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val date: LocalDate,
    val tags: String? = null // comma-separated tags
) 