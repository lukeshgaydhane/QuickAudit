package com.quickaudit.app.controller

import com.quickaudit.app.dto.*
import com.quickaudit.app.model.ChecklistItem
import com.quickaudit.app.model.ChecklistItemStatus
import com.quickaudit.app.model.ChecklistItemType
import com.quickaudit.app.service.ChecklistItemService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/checklist-items")
class ChecklistItemController(
    private val checklistItemService: ChecklistItemService
) {
    
    // Basic CRUD operations
    @PostMapping
    fun createChecklistItem(@Valid @RequestBody request: ChecklistItemRequest): ResponseEntity<ApiResponse<ChecklistItemResponse>> {
        return try {
            val checklistItem = ChecklistItem(
                audit = com.quickaudit.app.model.Audit(
                    id = request.auditId,
                    title = "Temp Title",
                    createdBy = com.quickaudit.app.model.User(
                        id = 1L,
                        username = "tempuser",
                        password = "temppass"
                    )
                ),
                question = request.question,
                answer = request.answer,
                status = request.status,
                type = request.type,
                orderIndex = request.orderIndex,
                notes = request.notes,
                category = request.category,
                subcategory = request.subcategory,
                isRequired = request.isRequired,
                answeredBy = request.answeredBy?.let { 
                    com.quickaudit.app.model.User(
                        id = it,
                        username = "tempuser",
                        password = "temppass"
                    )
                },
                evidence = request.evidence,
                riskLevel = request.riskLevel,
                recommendations = request.recommendations,
                isActive = request.isActive
            )
            
            val createdItem = checklistItemService.createChecklistItem(checklistItem)
            val response = mapToChecklistItemResponse(createdItem)
            
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "Checklist item created successfully",
                data = response
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = e.message ?: "Failed to create checklist item",
                errors = listOf(e.message ?: "Validation failed")
            ))
        }
    }
    
    @GetMapping("/{id}")
    fun getChecklistItem(@PathVariable id: Long): ResponseEntity<ApiResponse<ChecklistItemResponse>> {
        val item = checklistItemService.getChecklistItemById(id)
        return if (item != null) {
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "Checklist item retrieved successfully",
                data = mapToChecklistItemResponse(item)
            ))
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping
    fun getAllChecklistItems(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<List<ChecklistItemResponse>>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val items = checklistItemService.getAllChecklistItems()
        val responses = items.map { mapToChecklistItemResponse(it) }
        
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "Checklist items retrieved successfully",
            data = responses
        ))
    }
    
    @PutMapping("/{id}")
    fun updateChecklistItem(
        @PathVariable id: Long,
        @Valid @RequestBody request: ChecklistItemUpdateRequest
    ): ResponseEntity<ApiResponse<ChecklistItemResponse>> {
        return try {
            val existingItem = checklistItemService.getChecklistItemById(id)
                ?: return ResponseEntity.notFound().build()
            
            val updatedItem = existingItem.copy(
                question = request.question ?: existingItem.question,
                answer = request.answer ?: existingItem.answer,
                status = request.status ?: existingItem.status,
                type = request.type ?: existingItem.type,
                orderIndex = request.orderIndex ?: existingItem.orderIndex,
                notes = request.notes ?: existingItem.notes,
                category = request.category ?: existingItem.category,
                subcategory = request.subcategory ?: existingItem.subcategory,
                isRequired = request.isRequired ?: existingItem.isRequired,
                answeredBy = request.answeredBy?.let { 
                    com.quickaudit.app.model.User(
                        id = it,
                        username = "tempuser",
                        password = "temppass"
                    )
                } ?: existingItem.answeredBy,
                evidence = request.evidence ?: existingItem.evidence,
                riskLevel = request.riskLevel ?: existingItem.riskLevel,
                recommendations = request.recommendations ?: existingItem.recommendations,
                isActive = request.isActive ?: existingItem.isActive
            )
            
            val savedItem = checklistItemService.updateChecklistItem(id, updatedItem)
            return if (savedItem != null) {
                ResponseEntity.ok(ApiResponse(
                    success = true,
                    message = "Checklist item updated successfully",
                    data = mapToChecklistItemResponse(savedItem)
                ))
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = e.message ?: "Failed to update checklist item",
                errors = listOf(e.message ?: "Validation failed")
            ))
        }
    }
    
    @DeleteMapping("/{id}")
    fun deleteChecklistItem(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {
        val deleted = checklistItemService.deleteChecklistItem(id)
        return if (deleted) {
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "Checklist item deleted successfully"
            ))
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    // Audit-specific operations
    @GetMapping("/audit/{auditId}")
    fun getChecklistItemsByAuditId(
        @PathVariable auditId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<List<ChecklistItemResponse>>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val items = checklistItemService.getChecklistItemsByAuditIdWithPagination(auditId, pageable)
        val responses = items.content.map { mapToChecklistItemResponse(it) }
        
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "Checklist items retrieved successfully",
            data = responses
        ))
    }
    
    @PostMapping("/audit/{auditId}/bulk")
    fun createChecklistItemsForAudit(
        @PathVariable auditId: Long,
        @Valid @RequestBody requests: List<ChecklistItemRequest>
    ): ResponseEntity<ApiResponse<List<ChecklistItemResponse>>> {
        return try {
            val checklistItems = requests.map { request ->
                ChecklistItem(
                    audit = com.quickaudit.app.model.Audit(
                        id = auditId,
                        title = "Temp Title",
                        createdBy = com.quickaudit.app.model.User(
                            id = 1L,
                            username = "tempuser",
                            password = "temppass"
                        )
                    ),
                    question = request.question,
                    answer = request.answer,
                    status = request.status,
                    type = request.type,
                    orderIndex = request.orderIndex,
                    notes = request.notes,
                    category = request.category,
                    subcategory = request.subcategory,
                    isRequired = request.isRequired,
                    answeredBy = request.answeredBy?.let { 
                        com.quickaudit.app.model.User(
                            id = it,
                            username = "tempuser",
                            password = "temppass"
                        )
                    },
                    evidence = request.evidence,
                    riskLevel = request.riskLevel,
                    recommendations = request.recommendations,
                    isActive = request.isActive
                )
            }
            
            val createdItems = checklistItemService.createChecklistItemsForAudit(auditId, checklistItems)
            val responses = createdItems.map { mapToChecklistItemResponse(it) }
            
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "Checklist items created successfully",
                data = responses
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = e.message ?: "Failed to create checklist items",
                errors = listOf(e.message ?: "Validation failed")
            ))
        }
    }
    
    @DeleteMapping("/audit/{auditId}")
    fun deleteAllChecklistItemsByAuditId(@PathVariable auditId: Long): ResponseEntity<ApiResponse<Nothing>> {
        val deleted = checklistItemService.deleteAllChecklistItemsByAuditId(auditId)
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "All checklist items deleted successfully"
        ))
    }
    
    // Status-based operations
    @GetMapping("/audit/{auditId}/status/{status}")
    fun getChecklistItemsByAuditIdAndStatus(
        @PathVariable auditId: Long,
        @PathVariable status: ChecklistItemStatus
    ): ResponseEntity<ApiResponse<List<ChecklistItemResponse>>> {
        val items = checklistItemService.getChecklistItemsByAuditIdAndStatus(auditId, status)
        val responses = items.map { mapToChecklistItemResponse(it) }
        
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "Checklist items retrieved successfully",
            data = responses
        ))
    }
    
    @PutMapping("/{id}/status")
    fun updateChecklistItemStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: ChecklistItemStatusUpdateRequest
    ): ResponseEntity<ApiResponse<ChecklistItemResponse>> {
        val updatedItem = checklistItemService.updateChecklistItemStatus(id, request.status, request.answeredBy)
        return if (updatedItem != null) {
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "Checklist item status updated successfully",
                data = mapToChecklistItemResponse(updatedItem)
            ))
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    // Answer-based operations
    @PutMapping("/{id}/answer")
    fun updateChecklistItemAnswer(
        @PathVariable id: Long,
        @Valid @RequestBody request: ChecklistItemAnswerRequest
    ): ResponseEntity<ApiResponse<ChecklistItemResponse>> {
        return try {
            val updatedItem = checklistItemService.updateChecklistItemAnswer(id, request.answer)
            return if (updatedItem != null) {
                // Update additional fields if provided
                val finalItem = updatedItem.copy(
                    notes = request.notes ?: updatedItem.notes,
                    evidence = request.evidence ?: updatedItem.evidence,
                    recommendations = request.recommendations ?: updatedItem.recommendations
                )
                
                val savedItem = checklistItemService.updateChecklistItem(id, finalItem)
                ResponseEntity.ok(ApiResponse(
                    success = true,
                    message = "Checklist item answer updated successfully",
                    data = mapToChecklistItemResponse(savedItem!!)
                ))
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = e.message ?: "Failed to update checklist item answer",
                errors = listOf(e.message ?: "Validation failed")
            ))
        }
    }
    
    // Bulk operations
    @PutMapping("/audit/{auditId}/bulk/status")
    fun bulkUpdateChecklistItemStatus(
        @PathVariable auditId: Long,
        @Valid @RequestBody request: ChecklistItemBulkUpdateRequest
    ): ResponseEntity<ApiResponse<List<ChecklistItemResponse>>> {
        return try {
            val status = request.status ?: throw IllegalArgumentException("Status is required for bulk update")
            val updatedItems = checklistItemService.bulkUpdateChecklistItemStatus(auditId, request.itemIds, status, request.answeredBy)
            val responses = updatedItems.map { mapToChecklistItemResponse(it) }
            
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "Checklist items status updated successfully",
                data = responses
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = e.message ?: "Failed to update checklist items status",
                errors = listOf(e.message ?: "Validation failed")
            ))
        }
    }
    
    @PutMapping("/audit/{auditId}/bulk/answers")
    fun bulkUpdateChecklistItemAnswers(
        @PathVariable auditId: Long,
        @Valid @RequestBody request: ChecklistItemBulkAnswerRequest
    ): ResponseEntity<ApiResponse<List<ChecklistItemResponse>>> {
        return try {
            val updatedItems = checklistItemService.bulkUpdateChecklistItemAnswers(auditId, request.itemAnswers, request.answeredBy)
            val responses = updatedItems.map { mapToChecklistItemResponse(it) }
            
            ResponseEntity.ok(ApiResponse(
                success = true,
                message = "Checklist items answers updated successfully",
                data = responses
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ApiResponse(
                success = false,
                message = e.message ?: "Failed to update checklist items answers",
                errors = listOf(e.message ?: "Validation failed")
            ))
        }
    }
    
    // Statistics operations
    @GetMapping("/audit/{auditId}/statistics")
    fun getChecklistItemStatistics(@PathVariable auditId: Long): ResponseEntity<ApiResponse<ChecklistItemStatisticsResponse>> {
        val stats = checklistItemService.getChecklistItemStatisticsByAuditId(auditId)
        val response = ChecklistItemStatisticsResponse(
            totalItems = stats["totalItems"] as Long,
            completedItems = stats["completedItems"] as Long,
            pendingItems = stats["pendingItems"] as Long,
            skippedItems = stats["skippedItems"] as Long,
            notApplicableItems = stats["notApplicableItems"] as Long,
            requiredItems = stats["requiredItems"] as Long,
            optionalItems = stats["optionalItems"] as Long,
            completionRate = stats["completionRate"] as Double
        )
        
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "Checklist item statistics retrieved successfully",
            data = response
        ))
    }
    
    @GetMapping("/audit/{auditId}/progress")
    fun getChecklistItemProgress(@PathVariable auditId: Long): ResponseEntity<ApiResponse<ChecklistItemProgressResponse>> {
        val progress = checklistItemService.getChecklistItemProgressByAuditId(auditId)
        val response = ChecklistItemProgressResponse(
            totalItems = progress["totalItems"] as Long,
            completedItems = progress["completedItems"] as Long,
            remainingItems = progress["remainingItems"] as Long,
            progressPercentage = progress["progressPercentage"] as Double,
            isCompleted = progress["isCompleted"] as Boolean
        )
        
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "Checklist item progress retrieved successfully",
            data = response
        ))
    }
    
    // Search operations
    @GetMapping("/audit/{auditId}/search")
    fun searchChecklistItems(
        @PathVariable auditId: Long,
        @RequestParam searchTerm: String
    ): ResponseEntity<ApiResponse<List<ChecklistItemResponse>>> {
        val items = checklistItemService.searchChecklistItemsByAuditIdAndQuestion(auditId, searchTerm)
        val responses = items.map { mapToChecklistItemResponse(it) }
        
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "Search results retrieved successfully",
            data = responses
        ))
    }
    
    // Category operations
    @GetMapping("/audit/{auditId}/categories")
    fun getCategoriesByAuditId(@PathVariable auditId: Long): ResponseEntity<ApiResponse<List<String>>> {
        val categories = checklistItemService.getCategoriesByAuditId(auditId)
        
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "Categories retrieved successfully",
            data = categories
        ))
    }
    
    @GetMapping("/audit/{auditId}/categories/{category}/subcategories")
    fun getSubcategoriesByAuditIdAndCategory(
        @PathVariable auditId: Long,
        @PathVariable category: String
    ): ResponseEntity<ApiResponse<List<String>>> {
        val subcategories = checklistItemService.getSubcategoriesByAuditIdAndCategory(auditId, category)
        
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "Subcategories retrieved successfully",
            data = subcategories
        ))
    }
    
    // Reorder operations
    @PutMapping("/audit/{auditId}/reorder")
    fun reorderChecklistItems(
        @PathVariable auditId: Long,
        @Valid @RequestBody request: ChecklistItemReorderRequest
    ): ResponseEntity<ApiResponse<List<ChecklistItemResponse>>> {
        val reorderedItems = checklistItemService.reorderChecklistItems(auditId, request.itemIds)
        val responses = reorderedItems.map { mapToChecklistItemResponse(it) }
        
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "Checklist items reordered successfully",
            data = responses
        ))
    }
    
    // Helper method to map ChecklistItem to ChecklistItemResponse
    private fun mapToChecklistItemResponse(item: ChecklistItem): ChecklistItemResponse {
        return ChecklistItemResponse(
            id = item.id,
            auditId = item.audit.id,
            question = item.question,
            answer = item.answer,
            status = item.status,
            type = item.type,
            orderIndex = item.orderIndex,
            notes = item.notes,
            category = item.category,
            subcategory = item.subcategory,
            isRequired = item.isRequired,
            answeredBy = item.answeredBy?.let { user ->
                UserInfo(
                    id = user.id,
                    username = user.username,
                    email = user.email,
                    fullName = user.fullName,
                    role = user.role,
                    isActive = user.isActive
                )
            },
            answeredAt = item.answeredAt,
            evidence = item.evidence,
            riskLevel = item.riskLevel,
            recommendations = item.recommendations,
            isActive = item.isActive,
            createdAt = item.createdAt,
            updatedAt = item.updatedAt
        )
    }
} 