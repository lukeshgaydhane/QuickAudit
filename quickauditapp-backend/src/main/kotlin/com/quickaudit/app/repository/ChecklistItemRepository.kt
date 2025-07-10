package com.quickaudit.app.repository

import com.quickaudit.app.model.ChecklistItem
import com.quickaudit.app.model.ChecklistItemStatus
import com.quickaudit.app.model.ChecklistItemType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChecklistItemRepository : JpaRepository<ChecklistItem, Long> {
    
    // Find all checklist items for a specific audit
    fun findByAuditIdOrderByOrderIndexAsc(auditId: Long): List<ChecklistItem>
    
    // Find checklist items by audit ID and status
    fun findByAuditIdAndStatusOrderByOrderIndexAsc(auditId: Long, status: ChecklistItemStatus): List<ChecklistItem>
    
    // Find checklist items by audit ID and type
    fun findByAuditIdAndTypeOrderByOrderIndexAsc(auditId: Long, type: ChecklistItemType): List<ChecklistItem>
    
    // Find checklist items by audit ID and category
    fun findByAuditIdAndCategoryOrderByOrderIndexAsc(auditId: Long, category: String): List<ChecklistItem>
    
    // Find checklist items by audit ID and required status
    fun findByAuditIdAndIsRequiredOrderByOrderIndexAsc(auditId: Long, isRequired: Boolean): List<ChecklistItem>
    
    // Find checklist items by audit ID and active status
    fun findByAuditIdAndIsActiveOrderByOrderIndexAsc(auditId: Long, isActive: Boolean): List<ChecklistItem>
    
    // Find checklist items answered by a specific user
    fun findByAnsweredByIdOrderByAnsweredAtDesc(userId: Long): List<ChecklistItem>
    
    // Find checklist items by audit ID and risk level
    fun findByAuditIdAndRiskLevelOrderByOrderIndexAsc(auditId: Long, riskLevel: String): List<ChecklistItem>
    
    // Find checklist items with pending status for a specific audit
    fun findByAuditIdAndStatusInOrderByOrderIndexAsc(auditId: Long, statuses: List<ChecklistItemStatus>): List<ChecklistItem>
    
    // Count checklist items by audit ID and status
    fun countByAuditIdAndStatus(auditId: Long, status: ChecklistItemStatus): Long
    
    // Count completed checklist items for an audit
    fun countByAuditIdAndStatusIn(auditId: Long, statuses: List<ChecklistItemStatus>): Long
    
    // Find checklist items by audit ID with pagination
    fun findByAuditIdOrderByOrderIndexAsc(auditId: Long, pageable: org.springframework.data.domain.Pageable): org.springframework.data.domain.Page<ChecklistItem>
    
    // Search checklist items by question text (case-insensitive)
    @Query("SELECT ci FROM ChecklistItem ci WHERE ci.audit.id = :auditId AND LOWER(ci.question) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY ci.orderIndex ASC")
    fun findByAuditIdAndQuestionContainingIgnoreCaseOrderByOrderIndexAsc(@Param("auditId") auditId: Long, @Param("searchTerm") searchTerm: String): List<ChecklistItem>
    
    // Find checklist items by audit ID and answer text (case-insensitive)
    @Query("SELECT ci FROM ChecklistItem ci WHERE ci.audit.id = :auditId AND ci.answer IS NOT NULL AND LOWER(ci.answer) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY ci.orderIndex ASC")
    fun findByAuditIdAndAnswerContainingIgnoreCaseOrderByOrderIndexAsc(@Param("auditId") auditId: Long, @Param("searchTerm") searchTerm: String): List<ChecklistItem>
    
    // Find checklist items by audit ID and notes (case-insensitive)
    @Query("SELECT ci FROM ChecklistItem ci WHERE ci.audit.id = :auditId AND ci.notes IS NOT NULL AND LOWER(ci.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY ci.orderIndex ASC")
    fun findByAuditIdAndNotesContainingIgnoreCaseOrderByOrderIndexAsc(@Param("auditId") auditId: Long, @Param("searchTerm") searchTerm: String): List<ChecklistItem>
    
    // Find checklist items by audit ID and recommendations (case-insensitive)
    @Query("SELECT ci FROM ChecklistItem ci WHERE ci.audit.id = :auditId AND ci.recommendations IS NOT NULL AND LOWER(ci.recommendations) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY ci.orderIndex ASC")
    fun findByAuditIdAndRecommendationsContainingIgnoreCaseOrderByOrderIndexAsc(@Param("auditId") auditId: Long, @Param("searchTerm") searchTerm: String): List<ChecklistItem>
    
    // Find checklist items by audit ID and evidence (case-insensitive)
    @Query("SELECT ci FROM ChecklistItem ci WHERE ci.audit.id = :auditId AND ci.evidence IS NOT NULL AND LOWER(ci.evidence) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY ci.orderIndex ASC")
    fun findByAuditIdAndEvidenceContainingIgnoreCaseOrderByOrderIndexAsc(@Param("auditId") auditId: Long, @Param("searchTerm") searchTerm: String): List<ChecklistItem>
    
    // Find checklist items by audit ID and subcategory
    fun findByAuditIdAndSubcategoryOrderByOrderIndexAsc(auditId: Long, subcategory: String): List<ChecklistItem>
    
    // Find checklist items by audit ID and category and subcategory
    fun findByAuditIdAndCategoryAndSubcategoryOrderByOrderIndexAsc(auditId: Long, category: String, subcategory: String): List<ChecklistItem>
    
    // Find checklist items by audit ID and answered by user
    fun findByAuditIdAndAnsweredByIdOrderByAnsweredAtDesc(auditId: Long, userId: Long): List<ChecklistItem>
    
    // Find checklist items by audit ID and answered within date range
    @Query("SELECT ci FROM ChecklistItem ci WHERE ci.audit.id = :auditId AND ci.answeredAt BETWEEN :startDate AND :endDate ORDER BY ci.answeredAt DESC")
    fun findByAuditIdAndAnsweredAtBetweenOrderByAnsweredAtDesc(
        @Param("auditId") auditId: Long, 
        @Param("startDate") startDate: java.time.LocalDateTime, 
        @Param("endDate") endDate: java.time.LocalDateTime
    ): List<ChecklistItem>
    
    // Find checklist items by audit ID and created within date range
    @Query("SELECT ci FROM ChecklistItem ci WHERE ci.audit.id = :auditId AND ci.createdAt BETWEEN :startDate AND :endDate ORDER BY ci.createdAt DESC")
    fun findByAuditIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        @Param("auditId") auditId: Long, 
        @Param("startDate") startDate: java.time.LocalDateTime, 
        @Param("endDate") endDate: java.time.LocalDateTime
    ): List<ChecklistItem>
    
    // Find checklist items by audit ID and updated within date range
    @Query("SELECT ci FROM ChecklistItem ci WHERE ci.audit.id = :auditId AND ci.updatedAt BETWEEN :startDate AND :endDate ORDER BY ci.updatedAt DESC")
    fun findByAuditIdAndUpdatedAtBetweenOrderByUpdatedAtDesc(
        @Param("auditId") auditId: Long, 
        @Param("startDate") startDate: java.time.LocalDateTime, 
        @Param("endDate") endDate: java.time.LocalDateTime
    ): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index range
    fun findByAuditIdAndOrderIndexBetweenOrderByOrderIndexAsc(auditId: Long, startIndex: Int, endIndex: Int): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index greater than
    fun findByAuditIdAndOrderIndexGreaterThanOrderByOrderIndexAsc(auditId: Long, orderIndex: Int): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index less than
    fun findByAuditIdAndOrderIndexLessThanOrderByOrderIndexAsc(auditId: Long, orderIndex: Int): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index greater than or equal to
    fun findByAuditIdAndOrderIndexGreaterThanEqualOrderByOrderIndexAsc(auditId: Long, orderIndex: Int): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index less than or equal to
    fun findByAuditIdAndOrderIndexLessThanEqualOrderByOrderIndexAsc(auditId: Long, orderIndex: Int): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index
    fun findByAuditIdAndOrderIndexOrderByOrderIndexAsc(auditId: Long, orderIndex: Int): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index in
    fun findByAuditIdAndOrderIndexInOrderByOrderIndexAsc(auditId: Long, orderIndexes: List<Int>): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index not in
    fun findByAuditIdAndOrderIndexNotInOrderByOrderIndexAsc(auditId: Long, orderIndexes: List<Int>): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index is null
    fun findByAuditIdAndOrderIndexIsNullOrderByCreatedAtAsc(auditId: Long): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index is not null
    fun findByAuditIdAndOrderIndexIsNotNullOrderByOrderIndexAsc(auditId: Long): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index is not null and greater than
    fun findByAuditIdAndOrderIndexIsNotNullAndOrderIndexGreaterThanOrderByOrderIndexAsc(auditId: Long, orderIndex: Int): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index is not null and less than
    fun findByAuditIdAndOrderIndexIsNotNullAndOrderIndexLessThanOrderByOrderIndexAsc(auditId: Long, orderIndex: Int): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index is not null and between
    fun findByAuditIdAndOrderIndexIsNotNullAndOrderIndexBetweenOrderByOrderIndexAsc(auditId: Long, startIndex: Int, endIndex: Int): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index is not null and in
    fun findByAuditIdAndOrderIndexIsNotNullAndOrderIndexInOrderByOrderIndexAsc(auditId: Long, orderIndexes: List<Int>): List<ChecklistItem>
    
    // Find checklist items by audit ID and order index is not null and not in
    fun findByAuditIdAndOrderIndexIsNotNullAndOrderIndexNotInOrderByOrderIndexAsc(auditId: Long, orderIndexes: List<Int>): List<ChecklistItem>
} 