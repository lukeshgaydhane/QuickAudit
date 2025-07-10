package com.quickaudit.app.service

import com.quickaudit.app.model.Audit
import com.quickaudit.app.model.AuditStatus
import com.quickaudit.app.model.AuditPriority
import com.quickaudit.app.model.User
import java.time.LocalDateTime

interface AuditService {
    fun getAllAudits(): List<Audit>
    fun getAuditById(id: Long): Audit?
    fun createAudit(audit: Audit): Audit
    fun updateAudit(id: Long, audit: Audit): Audit?
    fun deleteAudit(id: Long): Boolean
    fun getAuditsByStatus(status: AuditStatus): List<Audit>
    fun getAuditsByPriority(priority: AuditPriority): List<Audit>
    fun getAuditsByUser(userId: Long): List<Audit>
    fun getAuditsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<Audit>
    fun assignAudit(auditId: Long, userId: Long): Audit?
    fun updateAuditStatus(auditId: Long, status: AuditStatus): Audit?
    fun getAuditsByTags(tags: List<String>): List<Audit>
    fun searchAudits(query: String): List<Audit>
    fun getOverdueAudits(): List<Audit>
    fun getUpcomingAudits(days: Int): List<Audit>
} 