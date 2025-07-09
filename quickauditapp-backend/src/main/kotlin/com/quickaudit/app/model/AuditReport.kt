package com.quickaudit.app.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "audit_reports")
data class AuditReport(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val title: String,
    val description: String,
    val date: LocalDate
) 