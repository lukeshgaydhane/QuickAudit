package com.quickaudit.app.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "reports")
data class Report(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @NotNull(message = "Audit ID is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_id", nullable = false)
    val audit: Audit,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val updatedAt: LocalDateTime? = null,
    
    @field:Size(max = 500, message = "Image URL must not exceed 500 characters")
    val imageUrl: String? = null,
    
    @field:Size(max = 100, message = "GPS location must not exceed 100 characters")
    val gpsLocation: String? = null, // e.g., "lat,lng"
    
    @field:Size(max = 500, message = "PDF URL must not exceed 500 characters")
    val pdfUrl: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ReportStatus = ReportStatus.DRAFT,
    
    @field:Size(max = 1000, message = "Summary must not exceed 1000 characters")
    val summary: String? = null,
    
    @field:Size(max = 2000, message = "Findings must not exceed 2000 characters")
    val findings: String? = null,
    
    @field:Size(max = 1000, message = "Recommendations must not exceed 1000 characters")
    val recommendations: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val severity: ReportSeverity = ReportSeverity.MEDIUM,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by", nullable = false)
    val generatedBy: User,
    
    @field:Size(max = 500, message = "Additional files must not exceed 500 characters")
    val additionalFiles: String? = null, // comma-separated file URLs
    
    val generatedAt: LocalDateTime? = null,
    
    val exportedAt: LocalDateTime? = null,
    
    @field:Size(max = 100, message = "Export format must not exceed 100 characters")
    val exportFormat: String? = null, // PDF, DOCX, etc.
    
    @field:Size(max = 500, message = "Export URL must not exceed 500 characters")
    val exportUrl: String? = null
)

enum class ReportStatus {
    DRAFT, IN_REVIEW, APPROVED, PUBLISHED, ARCHIVED
}

enum class ReportSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
} 