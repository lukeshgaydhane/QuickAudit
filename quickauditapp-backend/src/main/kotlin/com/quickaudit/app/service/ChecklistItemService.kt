package com.quickaudit.app.service

import com.quickaudit.app.model.ChecklistItem
import com.quickaudit.app.model.ChecklistItemStatus
import com.quickaudit.app.model.ChecklistItemType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface ChecklistItemService {
    
    // Basic CRUD operations
    fun createChecklistItem(checklistItem: ChecklistItem): ChecklistItem
    fun getChecklistItemById(id: Long): ChecklistItem?
    fun getAllChecklistItems(): List<ChecklistItem>
    fun updateChecklistItem(id: Long, checklistItem: ChecklistItem): ChecklistItem?
    fun deleteChecklistItem(id: Long): Boolean
    
    // Audit-specific operations
    fun getChecklistItemsByAuditId(auditId: Long): List<ChecklistItem>
    fun getChecklistItemsByAuditIdWithPagination(auditId: Long, pageable: Pageable): Page<ChecklistItem>
    fun createChecklistItemsForAudit(auditId: Long, checklistItems: List<ChecklistItem>): List<ChecklistItem>
    fun deleteAllChecklistItemsByAuditId(auditId: Long): Boolean
    
    // Status-based operations
    fun getChecklistItemsByAuditIdAndStatus(auditId: Long, status: ChecklistItemStatus): List<ChecklistItem>
    fun updateChecklistItemStatus(id: Long, status: ChecklistItemStatus, answeredBy: Long? = null): ChecklistItem?
    fun getChecklistItemsByAuditIdAndStatuses(auditId: Long, statuses: List<ChecklistItemStatus>): List<ChecklistItem>
    fun countChecklistItemsByAuditIdAndStatus(auditId: Long, status: ChecklistItemStatus): Long
    fun countCompletedChecklistItemsByAuditId(auditId: Long): Long
    
    // Type-based operations
    fun getChecklistItemsByAuditIdAndType(auditId: Long, type: ChecklistItemType): List<ChecklistItem>
    fun updateChecklistItemType(id: Long, type: ChecklistItemType): ChecklistItem?
    
    // Category-based operations
    fun getChecklistItemsByAuditIdAndCategory(auditId: Long, category: String): List<ChecklistItem>
    fun getChecklistItemsByAuditIdAndSubcategory(auditId: Long, subcategory: String): List<ChecklistItem>
    fun getChecklistItemsByAuditIdAndCategoryAndSubcategory(auditId: Long, category: String, subcategory: String): List<ChecklistItem>
    fun getCategoriesByAuditId(auditId: Long): List<String>
    fun getSubcategoriesByAuditIdAndCategory(auditId: Long, category: String): List<String>
    
    // Answer-based operations
    fun updateChecklistItemAnswer(id: Long, answer: String, answeredBy: Long? = null): ChecklistItem?
    fun getChecklistItemsByAuditIdAndAnswerContaining(auditId: Long, searchTerm: String): List<ChecklistItem>
    fun getAnsweredChecklistItemsByAuditId(auditId: Long): List<ChecklistItem>
    fun getUnansweredChecklistItemsByAuditId(auditId: Long): List<ChecklistItem>
    
    // User-based operations
    fun getChecklistItemsAnsweredByUser(userId: Long): List<ChecklistItem>
    fun getChecklistItemsByAuditIdAndAnsweredBy(auditId: Long, userId: Long): List<ChecklistItem>
    
    // Search operations
    fun searchChecklistItemsByAuditIdAndQuestion(auditId: Long, searchTerm: String): List<ChecklistItem>
    fun searchChecklistItemsByAuditIdAndNotes(auditId: Long, searchTerm: String): List<ChecklistItem>
    fun searchChecklistItemsByAuditIdAndRecommendations(auditId: Long, searchTerm: String): List<ChecklistItem>
    fun searchChecklistItemsByAuditIdAndEvidence(auditId: Long, searchTerm: String): List<ChecklistItem>
    
    // Risk-based operations
    fun getChecklistItemsByAuditIdAndRiskLevel(auditId: Long, riskLevel: String): List<ChecklistItem>
    fun updateChecklistItemRiskLevel(id: Long, riskLevel: String): ChecklistItem?
    fun getRiskLevelsByAuditId(auditId: Long): List<String>
    
    // Required/Active operations
    fun getRequiredChecklistItemsByAuditId(auditId: Long): List<ChecklistItem>
    fun getOptionalChecklistItemsByAuditId(auditId: Long): List<ChecklistItem>
    fun getActiveChecklistItemsByAuditId(auditId: Long): List<ChecklistItem>
    fun getInactiveChecklistItemsByAuditId(auditId: Long): List<ChecklistItem>
    fun updateChecklistItemRequiredStatus(id: Long, isRequired: Boolean): ChecklistItem?
    fun updateChecklistItemActiveStatus(id: Long, isActive: Boolean): ChecklistItem?
    
    // Order-based operations
    fun getChecklistItemsByAuditIdAndOrderIndexRange(auditId: Long, startIndex: Int, endIndex: Int): List<ChecklistItem>
    fun updateChecklistItemOrderIndex(id: Long, orderIndex: Int): ChecklistItem?
    fun reorderChecklistItems(auditId: Long, itemIds: List<Long>): List<ChecklistItem>
    fun getNextOrderIndexForAudit(auditId: Long): Int
    
    // Date-based operations
    fun getChecklistItemsByAuditIdAndAnsweredDateRange(auditId: Long, startDate: LocalDateTime, endDate: LocalDateTime): List<ChecklistItem>
    fun getChecklistItemsByAuditIdAndCreatedDateRange(auditId: Long, startDate: LocalDateTime, endDate: LocalDateTime): List<ChecklistItem>
    fun getChecklistItemsByAuditIdAndUpdatedDateRange(auditId: Long, startDate: LocalDateTime, endDate: LocalDateTime): List<ChecklistItem>
    
    // Bulk operations
    fun bulkUpdateChecklistItemStatus(auditId: Long, itemIds: List<Long>, status: ChecklistItemStatus, answeredBy: Long? = null): List<ChecklistItem>
    fun bulkUpdateChecklistItemAnswers(auditId: Long, itemAnswers: Map<Long, String>, answeredBy: Long? = null): List<ChecklistItem>
    fun bulkDeleteChecklistItems(auditId: Long, itemIds: List<Long>): Boolean
    
    // Validation operations
    fun validateChecklistItem(checklistItem: ChecklistItem): List<String>
    fun validateChecklistItemAnswer(id: Long, answer: String): List<String>
    fun isChecklistItemRequired(id: Long): Boolean
    fun canChecklistItemBeSkipped(id: Long): Boolean
    
    // Statistics operations
    fun getChecklistItemStatisticsByAuditId(auditId: Long): Map<String, Any>
    fun getChecklistItemProgressByAuditId(auditId: Long): Map<String, Any>
    fun getChecklistItemCompletionRateByAuditId(auditId: Long): Double
} 