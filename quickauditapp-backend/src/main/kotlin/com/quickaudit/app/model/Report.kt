package com.quickaudit.app.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reports")
data class Report(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val auditId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val imageUrl: String? = null,
    val gpsLocation: String? = null, // e.g., "lat,lng"
    val pdfUrl: String? = null
) 