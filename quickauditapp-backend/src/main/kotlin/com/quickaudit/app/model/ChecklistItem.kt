package com.quickaudit.app.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "checklist_items")
data class ChecklistItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_id", nullable = false)
    val audit: Audit,
    
    @field:NotBlank(message = "Question is required")
    @field:Size(max = 1000, message = "Question must not exceed 1000 characters")
    @Column(nullable = false)
    val question: String,
    
    @field:Size(max = 2000, message = "Answer must not exceed 2000 characters")
    val answer: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ChecklistItemStatus = ChecklistItemStatus.PENDING,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: ChecklistItemType = ChecklistItemType.YES_NO,
    
    @Column(nullable = false)
    val orderIndex: Int = 0,
    
    @field:Size(max = 500, message = "Notes must not exceed 500 characters")
    val notes: String? = null,
    
    @field:Size(max = 100, message = "Category must not exceed 100 characters")
    val category: String? = null,
    
    @field:Size(max = 100, message = "Subcategory must not exceed 100 characters")
    val subcategory: String? = null,
    
    @Column(nullable = false)
    val isRequired: Boolean = true,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val updatedAt: LocalDateTime? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answered_by")
    val answeredBy: User? = null,
    
    val answeredAt: LocalDateTime? = null,
    
    @field:Size(max = 1000, message = "Evidence must not exceed 1000 characters")
    val evidence: String? = null, // File paths or URLs to evidence
    
    @field:Size(max = 100, message = "Risk level must not exceed 100 characters")
    val riskLevel: String? = null,
    
    @field:Size(max = 500, message = "Recommendations must not exceed 500 characters")
    val recommendations: String? = null,
    
    @Column(nullable = false)
    val isActive: Boolean = true
)

enum class ChecklistItemStatus {
    PENDING, IN_PROGRESS, COMPLETED, SKIPPED, NOT_APPLICABLE
}

enum class ChecklistItemType {
    YES_NO, MULTIPLE_CHOICE, TEXT, NUMBER, DATE, FILE_UPLOAD, LOCATION, SIGNATURE
} 