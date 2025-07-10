package com.quickaudit.app.repository

import com.quickaudit.app.model.Report
import com.quickaudit.app.model.ReportStatus
import com.quickaudit.app.model.ReportSeverity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
 
@Repository
interface ReportRepository : JpaRepository<Report, Long> {
    fun findByAuditId(auditId: Long): List<Report>
    fun findByStatus(status: ReportStatus): List<Report>
    fun findBySeverity(severity: ReportSeverity): List<Report>
    fun findByGeneratedById(userId: Long): List<Report>
    fun findByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Report>
    fun findBySummaryContainingIgnoreCaseOrFindingsContainingIgnoreCase(summary: String, findings: String): List<Report>
    fun findByImageUrlIsNotNull(): List<Report>
    fun findByGpsLocationIsNotNull(): List<Report>
} 