package com.quickaudit.app.repository

import com.quickaudit.app.model.Audit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuditRepository : JpaRepository<Audit, Long> 