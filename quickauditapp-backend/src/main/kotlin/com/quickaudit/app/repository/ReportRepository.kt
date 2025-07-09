package com.quickaudit.app.repository

import com.quickaudit.app.model.Report
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository : JpaRepository<Report, Long> {
    fun findByAuditId(auditId: Long): List<Report>
} 