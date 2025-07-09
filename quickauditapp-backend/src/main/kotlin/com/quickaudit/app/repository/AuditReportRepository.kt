package com.quickaudit.app.repository

import com.quickaudit.app.model.AuditReport
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuditReportRepository : JpaRepository<AuditReport, Long> 