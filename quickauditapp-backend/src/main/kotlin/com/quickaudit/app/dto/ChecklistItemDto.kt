package com.quickaudit.app.dto

import com.quickaudit.app.model.ChecklistItemStatus
import com.quickaudit.app.model.ChecklistItemType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class ChecklistItemRequest(
    @field:NotNull(message = "Audit ID is required")
    val auditId: Long,
    
    @field:NotBlank(message = "Question is required")
    @field:Size(max = 1000, message = "Question must not exceed 1000 characters")
    val question: String,
    
    @field:Size(max = 2000, message = "Answer must not exceed 2000 characters")
    val answer: String? = null,
    
    val status: ChecklistItemStatus = ChecklistItemStatus.PENDING,
    
    val type: ChecklistItemType = ChecklistItemType.YES_NO,
    
    val orderIndex: Int = 0,
    
    @field:Size(max = 500, message = "Notes must not exceed 500 characters")
    val notes: String? = null,
    
    @field:Size(max = 100, message = "Category must not exceed 100 characters")
    val category: String? = null,
    
    @field:Size(max = 100, message = "Subcategory must not exceed 100 characters")
    val subcategory: String? = null,
    
    val isRequired: Boolean = true,
    
    val answeredBy: Long? = null,
    
    @field:Size(max = 1000, message = "Evidence must not exceed 1000 characters")
    val evidence: String? = null,
    
    @field:Size(max = 100, message = "Risk level must not exceed 100 characters")
    val riskLevel: String? = null,
    
    @field:Size(max = 500, message = "Recommendations must not exceed 500 characters")
    val recommendations: String? = null,
    
    val isActive: Boolean = true
)

data class ChecklistItemResponse(
    val id: Long,
    val auditId: Long,
    val question: String,
    val answer: String?,
    val status: ChecklistItemStatus,
    val type: ChecklistItemType,
    val orderIndex: Int,
    val notes: String?,
    val category: String?,
    val subcategory: String?,
    val isRequired: Boolean,
    val answeredBy: UserInfo?,
    val answeredAt: LocalDateTime?,
    val evidence: String?,
    val riskLevel: String?,
    val recommendations: String?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)

data class ChecklistItemUpdateRequest(
    @field:Size(max = 1000, message = "Question must not exceed 1000 characters")
    val question: String? = null,
    
    @field:Size(max = 2000, message = "Answer must not exceed 2000 characters")
    val answer: String? = null,
    
    val status: ChecklistItemStatus? = null,
    
    val type: ChecklistItemType? = null,
    
    val orderIndex: Int? = null,
    
    @field:Size(max = 500, message = "Notes must not exceed 500 characters")
    val notes: String? = null,
    
    @field:Size(max = 100, message = "Category must not exceed 100 characters")
    val category: String? = null,
    
    @field:Size(max = 100, message = "Subcategory must not exceed 100 characters")
    val subcategory: String? = null,
    
    val isRequired: Boolean? = null,
    
    val answeredBy: Long? = null,
    
    @field:Size(max = 1000, message = "Evidence must not exceed 1000 characters")
    val evidence: String? = null,
    
    @field:Size(max = 100, message = "Risk level must not exceed 100 characters")
    val riskLevel: String? = null,
    
    @field:Size(max = 500, message = "Recommendations must not exceed 500 characters")
    val recommendations: String? = null,
    
    val isActive: Boolean? = null
)

data class ChecklistItemAnswerRequest(
    @field:NotBlank(message = "Answer is required")
    @field:Size(max = 2000, message = "Answer must not exceed 2000 characters")
    val answer: String,
    
    @field:Size(max = 500, message = "Notes must not exceed 500 characters")
    val notes: String? = null,
    
    @field:Size(max = 1000, message = "Evidence must not exceed 1000 characters")
    val evidence: String? = null,
    
    @field:Size(max = 500, message = "Recommendations must not exceed 500 characters")
    val recommendations: String? = null
)

data class ChecklistItemStatusUpdateRequest(
    @field:NotNull(message = "Status is required")
    val status: ChecklistItemStatus,
    
    val answeredBy: Long? = null
)

data class ChecklistItemBulkUpdateRequest(
    @field:NotNull(message = "Item IDs are required")
    val itemIds: List<Long>,
    
    val status: ChecklistItemStatus? = null,
    
    val answeredBy: Long? = null
)

data class ChecklistItemBulkAnswerRequest(
    @field:NotNull(message = "Item answers are required")
    val itemAnswers: Map<Long, String>,
    
    val answeredBy: Long? = null
)

data class ChecklistItemSearchRequest(
    val searchTerm: String? = null,
    val status: ChecklistItemStatus? = null,
    val type: ChecklistItemType? = null,
    val category: String? = null,
    val subcategory: String? = null,
    val isRequired: Boolean? = null,
    val isActive: Boolean? = null,
    val riskLevel: String? = null,
    val answeredBy: Long? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null
)

data class ChecklistItemStatisticsResponse(
    val totalItems: Long,
    val completedItems: Long,
    val pendingItems: Long,
    val skippedItems: Long,
    val notApplicableItems: Long,
    val requiredItems: Long,
    val optionalItems: Long,
    val completionRate: Double
)

data class ChecklistItemProgressResponse(
    val totalItems: Long,
    val completedItems: Long,
    val remainingItems: Long,
    val progressPercentage: Double,
    val isCompleted: Boolean
)

data class ChecklistItemReorderRequest(
    @field:NotNull(message = "Item IDs are required")
    val itemIds: List<Long>
) 