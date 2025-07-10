package com.quickaudit.app.service.impl

import com.quickaudit.app.model.ChecklistItem
import com.quickaudit.app.model.ChecklistItemStatus
import com.quickaudit.app.model.ChecklistItemType
import com.quickaudit.app.repository.AuditRepository
import com.quickaudit.app.repository.ChecklistItemRepository
import com.quickaudit.app.repository.UserRepository
import com.quickaudit.app.service.ChecklistItemService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ChecklistItemServiceImpl(
    private val checklistItemRepository: ChecklistItemRepository,
    private val auditRepository: AuditRepository,
    private val userRepository: UserRepository
) : ChecklistItemService {
    
    // Basic CRUD operations
    override fun createChecklistItem(checklistItem: ChecklistItem): ChecklistItem {
        val errors = validateChecklistItem(checklistItem)
        if (errors.isNotEmpty()) {
            throw IllegalArgumentException("Validation errors: ${errors.joinToString(", ")}")
        }
        
        val audit = auditRepository.findById(checklistItem.audit.id)
            .orElseThrow { IllegalArgumentException("Audit not found with ID: ${checklistItem.audit.id}") }
        
        val answeredBy = checklistItem.answeredBy?.let { userRepository.findById(it.id).orElse(null) }
        
        val newChecklistItem = checklistItem.copy(
            audit = audit,
            answeredBy = answeredBy,
            orderIndex = if (checklistItem.orderIndex == 0) getNextOrderIndexForAudit(audit.id) else checklistItem.orderIndex
        )
        
        return checklistItemRepository.save(newChecklistItem)
    }
    
    override fun getChecklistItemById(id: Long): ChecklistItem? {
        return checklistItemRepository.findById(id).orElse(null)
    }
    
    override fun getAllChecklistItems(): List<ChecklistItem> {
        return checklistItemRepository.findAll()
    }
    
    override fun updateChecklistItem(id: Long, checklistItem: ChecklistItem): ChecklistItem? {
        val existingItem = getChecklistItemById(id) ?: return null
        
        val errors = validateChecklistItem(checklistItem)
        if (errors.isNotEmpty()) {
            throw IllegalArgumentException("Validation errors: ${errors.joinToString(", ")}")
        }
        
        val audit = auditRepository.findById(checklistItem.audit.id)
            .orElseThrow { IllegalArgumentException("Audit not found with ID: ${checklistItem.audit.id}") }
        
        val answeredBy = checklistItem.answeredBy?.let { userRepository.findById(it.id).orElse(null) }
        
        val updatedItem = existingItem.copy(
            audit = audit,
            question = checklistItem.question,
            answer = checklistItem.answer,
            status = checklistItem.status,
            type = checklistItem.type,
            orderIndex = checklistItem.orderIndex,
            notes = checklistItem.notes,
            category = checklistItem.category,
            subcategory = checklistItem.subcategory,
            isRequired = checklistItem.isRequired,
            answeredBy = answeredBy,
            answeredAt = checklistItem.answeredAt,
            evidence = checklistItem.evidence,
            riskLevel = checklistItem.riskLevel,
            recommendations = checklistItem.recommendations,
            isActive = checklistItem.isActive,
            updatedAt = LocalDateTime.now()
        )
        
        return checklistItemRepository.save(updatedItem)
    }
    
    override fun deleteChecklistItem(id: Long): Boolean {
        return if (checklistItemRepository.existsById(id)) {
            checklistItemRepository.deleteById(id)
            true
        } else {
            false
        }
    }
    
    // Audit-specific operations
    override fun getChecklistItemsByAuditId(auditId: Long): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdOrderByOrderIndexAsc(auditId)
    }
    
    override fun getChecklistItemsByAuditIdWithPagination(auditId: Long, pageable: Pageable): Page<ChecklistItem> {
        return checklistItemRepository.findByAuditIdOrderByOrderIndexAsc(auditId, pageable)
    }
    
    override fun createChecklistItemsForAudit(auditId: Long, checklistItems: List<ChecklistItem>): List<ChecklistItem> {
        val audit = auditRepository.findById(auditId)
            .orElseThrow { IllegalArgumentException("Audit not found with ID: $auditId") }
        
        val savedItems = mutableListOf<ChecklistItem>()
        var orderIndex = getNextOrderIndexForAudit(auditId)
        
        for (item in checklistItems) {
            val newItem = item.copy(
                audit = audit,
                orderIndex = orderIndex++
            )
            savedItems.add(createChecklistItem(newItem))
        }
        
        return savedItems
    }
    
    override fun deleteAllChecklistItemsByAuditId(auditId: Long): Boolean {
        val items = getChecklistItemsByAuditId(auditId)
        checklistItemRepository.deleteAll(items)
        return true
    }
    
    // Status-based operations
    override fun getChecklistItemsByAuditIdAndStatus(auditId: Long, status: ChecklistItemStatus): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndStatusOrderByOrderIndexAsc(auditId, status)
    }
    
    override fun updateChecklistItemStatus(id: Long, status: ChecklistItemStatus, answeredBy: Long?): ChecklistItem? {
        val item = getChecklistItemById(id) ?: return null
        
        val user = answeredBy?.let { userRepository.findById(it).orElse(null) }
        val answeredAt = if (status == ChecklistItemStatus.COMPLETED) LocalDateTime.now() else item.answeredAt
        
        val updatedItem = item.copy(
            status = status,
            answeredBy = user,
            answeredAt = answeredAt,
            updatedAt = LocalDateTime.now()
        )
        
        return checklistItemRepository.save(updatedItem)
    }
    
    override fun getChecklistItemsByAuditIdAndStatuses(auditId: Long, statuses: List<ChecklistItemStatus>): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndStatusInOrderByOrderIndexAsc(auditId, statuses)
    }
    
    override fun countChecklistItemsByAuditIdAndStatus(auditId: Long, status: ChecklistItemStatus): Long {
        return checklistItemRepository.countByAuditIdAndStatus(auditId, status)
    }
    
    override fun countCompletedChecklistItemsByAuditId(auditId: Long): Long {
        return checklistItemRepository.countByAuditIdAndStatusIn(auditId, listOf(ChecklistItemStatus.COMPLETED))
    }
    
    // Type-based operations
    override fun getChecklistItemsByAuditIdAndType(auditId: Long, type: ChecklistItemType): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndTypeOrderByOrderIndexAsc(auditId, type)
    }
    
    override fun updateChecklistItemType(id: Long, type: ChecklistItemType): ChecklistItem? {
        val item = getChecklistItemById(id) ?: return null
        
        val updatedItem = item.copy(
            type = type,
            updatedAt = LocalDateTime.now()
        )
        
        return checklistItemRepository.save(updatedItem)
    }
    
    // Category-based operations
    override fun getChecklistItemsByAuditIdAndCategory(auditId: Long, category: String): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndCategoryOrderByOrderIndexAsc(auditId, category)
    }
    
    override fun getChecklistItemsByAuditIdAndSubcategory(auditId: Long, subcategory: String): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndSubcategoryOrderByOrderIndexAsc(auditId, subcategory)
    }
    
    override fun getChecklistItemsByAuditIdAndCategoryAndSubcategory(auditId: Long, category: String, subcategory: String): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndCategoryAndSubcategoryOrderByOrderIndexAsc(auditId, category, subcategory)
    }
    
    override fun getCategoriesByAuditId(auditId: Long): List<String> {
        return getChecklistItemsByAuditId(auditId)
            .mapNotNull { it.category }
            .distinct()
            .sorted()
    }
    
    override fun getSubcategoriesByAuditIdAndCategory(auditId: Long, category: String): List<String> {
        return getChecklistItemsByAuditIdAndCategory(auditId, category)
            .mapNotNull { it.subcategory }
            .distinct()
            .sorted()
    }
    
    // Answer-based operations
    override fun updateChecklistItemAnswer(id: Long, answer: String, answeredBy: Long?): ChecklistItem? {
        val item = getChecklistItemById(id) ?: return null
        
        val errors = validateChecklistItemAnswer(id, answer)
        if (errors.isNotEmpty()) {
            throw IllegalArgumentException("Answer validation errors: ${errors.joinToString(", ")}")
        }
        
        val user = answeredBy?.let { userRepository.findById(it).orElse(null) }
        val answeredAt = LocalDateTime.now()
        
        val updatedItem = item.copy(
            answer = answer,
            status = ChecklistItemStatus.COMPLETED,
            answeredBy = user,
            answeredAt = answeredAt,
            updatedAt = LocalDateTime.now()
        )
        
        return checklistItemRepository.save(updatedItem)
    }
    
    override fun getChecklistItemsByAuditIdAndAnswerContaining(auditId: Long, searchTerm: String): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndAnswerContainingIgnoreCaseOrderByOrderIndexAsc(auditId, searchTerm)
    }
    
    override fun getAnsweredChecklistItemsByAuditId(auditId: Long): List<ChecklistItem> {
        return getChecklistItemsByAuditId(auditId).filter { it.answer != null }
    }
    
    override fun getUnansweredChecklistItemsByAuditId(auditId: Long): List<ChecklistItem> {
        return getChecklistItemsByAuditId(auditId).filter { it.answer == null }
    }
    
    // User-based operations
    override fun getChecklistItemsAnsweredByUser(userId: Long): List<ChecklistItem> {
        return checklistItemRepository.findByAnsweredByIdOrderByAnsweredAtDesc(userId)
    }
    
    override fun getChecklistItemsByAuditIdAndAnsweredBy(auditId: Long, userId: Long): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndAnsweredByIdOrderByAnsweredAtDesc(auditId, userId)
    }
    
    // Search operations
    override fun searchChecklistItemsByAuditIdAndQuestion(auditId: Long, searchTerm: String): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndQuestionContainingIgnoreCaseOrderByOrderIndexAsc(auditId, searchTerm)
    }
    
    override fun searchChecklistItemsByAuditIdAndNotes(auditId: Long, searchTerm: String): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndNotesContainingIgnoreCaseOrderByOrderIndexAsc(auditId, searchTerm)
    }
    
    override fun searchChecklistItemsByAuditIdAndRecommendations(auditId: Long, searchTerm: String): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndRecommendationsContainingIgnoreCaseOrderByOrderIndexAsc(auditId, searchTerm)
    }
    
    override fun searchChecklistItemsByAuditIdAndEvidence(auditId: Long, searchTerm: String): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndEvidenceContainingIgnoreCaseOrderByOrderIndexAsc(auditId, searchTerm)
    }
    
    // Risk-based operations
    override fun getChecklistItemsByAuditIdAndRiskLevel(auditId: Long, riskLevel: String): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndRiskLevelOrderByOrderIndexAsc(auditId, riskLevel)
    }
    
    override fun updateChecklistItemRiskLevel(id: Long, riskLevel: String): ChecklistItem? {
        val item = getChecklistItemById(id) ?: return null
        
        val updatedItem = item.copy(
            riskLevel = riskLevel,
            updatedAt = LocalDateTime.now()
        )
        
        return checklistItemRepository.save(updatedItem)
    }
    
    override fun getRiskLevelsByAuditId(auditId: Long): List<String> {
        return getChecklistItemsByAuditId(auditId)
            .mapNotNull { it.riskLevel }
            .distinct()
            .sorted()
    }
    
    // Required/Active operations
    override fun getRequiredChecklistItemsByAuditId(auditId: Long): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndIsRequiredOrderByOrderIndexAsc(auditId, true)
    }
    
    override fun getOptionalChecklistItemsByAuditId(auditId: Long): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndIsRequiredOrderByOrderIndexAsc(auditId, false)
    }
    
    override fun getActiveChecklistItemsByAuditId(auditId: Long): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndIsActiveOrderByOrderIndexAsc(auditId, true)
    }
    
    override fun getInactiveChecklistItemsByAuditId(auditId: Long): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndIsActiveOrderByOrderIndexAsc(auditId, false)
    }
    
    override fun updateChecklistItemRequiredStatus(id: Long, isRequired: Boolean): ChecklistItem? {
        val item = getChecklistItemById(id) ?: return null
        
        val updatedItem = item.copy(
            isRequired = isRequired,
            updatedAt = LocalDateTime.now()
        )
        
        return checklistItemRepository.save(updatedItem)
    }
    
    override fun updateChecklistItemActiveStatus(id: Long, isActive: Boolean): ChecklistItem? {
        val item = getChecklistItemById(id) ?: return null
        
        val updatedItem = item.copy(
            isActive = isActive,
            updatedAt = LocalDateTime.now()
        )
        
        return checklistItemRepository.save(updatedItem)
    }
    
    // Order-based operations
    override fun getChecklistItemsByAuditIdAndOrderIndexRange(auditId: Long, startIndex: Int, endIndex: Int): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndOrderIndexBetweenOrderByOrderIndexAsc(auditId, startIndex, endIndex)
    }
    
    override fun updateChecklistItemOrderIndex(id: Long, orderIndex: Int): ChecklistItem? {
        val item = getChecklistItemById(id) ?: return null
        
        val updatedItem = item.copy(
            orderIndex = orderIndex,
            updatedAt = LocalDateTime.now()
        )
        
        return checklistItemRepository.save(updatedItem)
    }
    
    override fun reorderChecklistItems(auditId: Long, itemIds: List<Long>): List<ChecklistItem> {
        val updatedItems = mutableListOf<ChecklistItem>()
        
        for ((index, itemId) in itemIds.withIndex()) {
            val item = updateChecklistItemOrderIndex(itemId, index + 1)
            item?.let { updatedItems.add(it) }
        }
        
        return updatedItems
    }
    
    override fun getNextOrderIndexForAudit(auditId: Long): Int {
        val items = getChecklistItemsByAuditId(auditId)
        return if (items.isEmpty()) 1 else items.maxOf { it.orderIndex } + 1
    }
    
    // Date-based operations
    override fun getChecklistItemsByAuditIdAndAnsweredDateRange(auditId: Long, startDate: LocalDateTime, endDate: LocalDateTime): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndAnsweredAtBetweenOrderByAnsweredAtDesc(auditId, startDate, endDate)
    }
    
    override fun getChecklistItemsByAuditIdAndCreatedDateRange(auditId: Long, startDate: LocalDateTime, endDate: LocalDateTime): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndCreatedAtBetweenOrderByCreatedAtDesc(auditId, startDate, endDate)
    }
    
    override fun getChecklistItemsByAuditIdAndUpdatedDateRange(auditId: Long, startDate: LocalDateTime, endDate: LocalDateTime): List<ChecklistItem> {
        return checklistItemRepository.findByAuditIdAndUpdatedAtBetweenOrderByUpdatedAtDesc(auditId, startDate, endDate)
    }
    
    // Bulk operations
    override fun bulkUpdateChecklistItemStatus(auditId: Long, itemIds: List<Long>, status: ChecklistItemStatus, answeredBy: Long?): List<ChecklistItem> {
        val updatedItems = mutableListOf<ChecklistItem>()
        
        for (itemId in itemIds) {
            val item = updateChecklistItemStatus(itemId, status, answeredBy)
            item?.let { updatedItems.add(it) }
        }
        
        return updatedItems
    }
    
    override fun bulkUpdateChecklistItemAnswers(auditId: Long, itemAnswers: Map<Long, String>, answeredBy: Long?): List<ChecklistItem> {
        val updatedItems = mutableListOf<ChecklistItem>()
        
        for ((itemId, answer) in itemAnswers) {
            val item = updateChecklistItemAnswer(itemId, answer, answeredBy)
            item?.let { updatedItems.add(it) }
        }
        
        return updatedItems
    }
    
    override fun bulkDeleteChecklistItems(auditId: Long, itemIds: List<Long>): Boolean {
        val items = getChecklistItemsByAuditId(auditId).filter { itemIds.contains(it.id) }
        checklistItemRepository.deleteAll(items)
        return true
    }
    
    // Validation operations
    override fun validateChecklistItem(checklistItem: ChecklistItem): List<String> {
        val errors = mutableListOf<String>()
        
        if (checklistItem.question.isBlank()) {
            errors.add("Question is required")
        }
        
        if (checklistItem.question.length > 1000) {
            errors.add("Question must not exceed 1000 characters")
        }
        
        if (checklistItem.answer?.length ?: 0 > 2000) {
            errors.add("Answer must not exceed 2000 characters")
        }
        
        if (checklistItem.notes?.length ?: 0 > 500) {
            errors.add("Notes must not exceed 500 characters")
        }
        
        if (checklistItem.category?.length ?: 0 > 100) {
            errors.add("Category must not exceed 100 characters")
        }
        
        if (checklistItem.subcategory?.length ?: 0 > 100) {
            errors.add("Subcategory must not exceed 100 characters")
        }
        
        if (checklistItem.evidence?.length ?: 0 > 1000) {
            errors.add("Evidence must not exceed 1000 characters")
        }
        
        if (checklistItem.riskLevel?.length ?: 0 > 100) {
            errors.add("Risk level must not exceed 100 characters")
        }
        
        if (checklistItem.recommendations?.length ?: 0 > 500) {
            errors.add("Recommendations must not exceed 500 characters")
        }
        
        return errors
    }
    
    override fun validateChecklistItemAnswer(id: Long, answer: String): List<String> {
        val errors = mutableListOf<String>()
        
        if (answer.length > 2000) {
            errors.add("Answer must not exceed 2000 characters")
        }
        
        val item = getChecklistItemById(id)
        if (item != null) {
            when (item.type) {
                ChecklistItemType.YES_NO -> {
                    if (!answer.equals("yes", ignoreCase = true) && !answer.equals("no", ignoreCase = true)) {
                        errors.add("Answer must be 'yes' or 'no' for YES_NO type")
                    }
                }
                ChecklistItemType.NUMBER -> {
                    if (!answer.matches(Regex("^\\d+(\\.\\d+)?$"))) {
                        errors.add("Answer must be a valid number for NUMBER type")
                    }
                }
                ChecklistItemType.DATE -> {
                    try {
                        LocalDateTime.parse(answer)
                    } catch (e: Exception) {
                        errors.add("Answer must be a valid date for DATE type")
                    }
                }
                else -> {
                    // Other types accept any string
                }
            }
        }
        
        return errors
    }
    
    override fun isChecklistItemRequired(id: Long): Boolean {
        val item = getChecklistItemById(id)
        return item?.isRequired ?: false
    }
    
    override fun canChecklistItemBeSkipped(id: Long): Boolean {
        val item = getChecklistItemById(id)
        return item?.let { !it.isRequired || it.status == ChecklistItemStatus.NOT_APPLICABLE } ?: false
    }
    
    // Statistics operations
    override fun getChecklistItemStatisticsByAuditId(auditId: Long): Map<String, Any> {
        val items = getChecklistItemsByAuditId(auditId)
        
        val totalItems = items.size.toLong()
        val completedItems = items.count { it.status == ChecklistItemStatus.COMPLETED }.toLong()
        val pendingItems = items.count { it.status == ChecklistItemStatus.PENDING }.toLong()
        val skippedItems = items.count { it.status == ChecklistItemStatus.SKIPPED }.toLong()
        val notApplicableItems = items.count { it.status == ChecklistItemStatus.NOT_APPLICABLE }.toLong()
        val requiredItems = items.count { it.isRequired }.toLong()
        val optionalItems = totalItems - requiredItems
        
        return mapOf(
            "totalItems" to totalItems,
            "completedItems" to completedItems,
            "pendingItems" to pendingItems,
            "skippedItems" to skippedItems,
            "notApplicableItems" to notApplicableItems,
            "requiredItems" to requiredItems,
            "optionalItems" to optionalItems,
            "completionRate" to if (totalItems > 0) (completedItems.toDouble() / totalItems) * 100 else 0.0
        )
    }
    
    override fun getChecklistItemProgressByAuditId(auditId: Long): Map<String, Any> {
        val stats = getChecklistItemStatisticsByAuditId(auditId)
        val totalItems = stats["totalItems"] as Long
        val completedItems = stats["completedItems"] as Long
        
        return mapOf(
            "totalItems" to totalItems,
            "completedItems" to completedItems,
            "remainingItems" to (totalItems - completedItems),
            "progressPercentage" to if (totalItems > 0) (completedItems.toDouble() / totalItems) * 100 else 0.0,
            "isCompleted" to (totalItems > 0 && completedItems == totalItems)
        )
    }
    
    override fun getChecklistItemCompletionRateByAuditId(auditId: Long): Double {
        val stats = getChecklistItemStatisticsByAuditId(auditId)
        return stats["completionRate"] as Double
    }
} 