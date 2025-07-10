package com.quickaudit.app.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "audits")
data class Audit(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(nullable = false)
    val title: String,
    
    @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String? = null,
    
    @Column(nullable = false)
    val auditDate: LocalDateTime = LocalDateTime.now(),
    
    @field:Size(max = 500, message = "Tags must not exceed 500 characters")
    val tags: String? = null, // comma-separated tags
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: AuditStatus = AuditStatus.DRAFT,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    val createdBy: User,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    val assignedTo: User? = null,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val updatedAt: LocalDateTime? = null,
    
    @field:Size(max = 200, message = "Location must not exceed 200 characters")
    val location: String? = null,
    
    @field:Size(max = 100, message = "Department must not exceed 100 characters")
    val department: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val priority: AuditPriority = AuditPriority.MEDIUM,
    
    @field:Size(max = 1000, message = "Notes must not exceed 1000 characters")
    val notes: String? = null,
    
    val scheduledDate: LocalDateTime? = null,
    
    val completedDate: LocalDateTime? = null,
    
    @OneToMany(mappedBy = "audit", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val checklistItems: MutableList<ChecklistItem> = mutableListOf()
)

enum class AuditStatus {
    DRAFT, IN_PROGRESS, COMPLETED, CANCELLED, ARCHIVED
}

enum class AuditPriority {
    LOW, MEDIUM, HIGH, CRITICAL
} 