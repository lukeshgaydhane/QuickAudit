package com.quickaudit.app.repository

import com.quickaudit.app.model.Audit
import com.quickaudit.app.model.AuditStatus
import com.quickaudit.app.model.AuditPriority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
 
@Repository
interface AuditRepository : JpaRepository<Audit, Long> {
    fun findByStatus(status: AuditStatus): List<Audit>
    fun findByPriority(priority: AuditPriority): List<Audit>
    fun findByCreatedByIdOrAssignedToId(createdById: Long, assignedToId: Long): List<Audit>
    fun findByAuditDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Audit>
    fun findByTagsContaining(tags: String): List<Audit>
    fun findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(title: String, description: String): List<Audit>
    fun findByScheduledDateBeforeAndStatusNot(scheduledDate: LocalDateTime, status: AuditStatus): List<Audit>
    fun findByScheduledDateBetweenAndStatusNot(startDate: LocalDateTime, endDate: LocalDateTime, status: AuditStatus): List<Audit>
} 