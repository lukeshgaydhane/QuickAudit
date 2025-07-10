package com.quickaudit.app.controller

import com.quickaudit.app.model.Audit
import com.quickaudit.app.model.AuditStatus
import com.quickaudit.app.model.AuditPriority
import com.quickaudit.app.service.AuditService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/audits")
class AuditController(private val auditService: AuditService) {
    
    @GetMapping
    fun getAllAudits(): List<Audit> = auditService.getAllAudits()
    
    @GetMapping("/{id}")
    fun getAuditById(@PathVariable id: Long): ResponseEntity<Audit> =
        auditService.getAuditById(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    
    @PostMapping
    fun createAudit(@Valid @RequestBody audit: Audit): ResponseEntity<Audit> {
        return try {
            val createdAudit = auditService.createAudit(audit)
            ResponseEntity.ok(createdAudit)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @PutMapping("/{id}")
    fun updateAudit(@PathVariable id: Long, @Valid @RequestBody audit: Audit): ResponseEntity<Audit> {
        return try {
            val updatedAudit = auditService.updateAudit(id, audit)
            updatedAudit?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @DeleteMapping("/{id}")
    fun deleteAudit(@PathVariable id: Long): ResponseEntity<Void> {
        return if (auditService.deleteAudit(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/status/{status}")
    fun getAuditsByStatus(@PathVariable status: AuditStatus): List<Audit> = 
        auditService.getAuditsByStatus(status)
    
    @GetMapping("/priority/{priority}")
    fun getAuditsByPriority(@PathVariable priority: AuditPriority): List<Audit> = 
        auditService.getAuditsByPriority(priority)
    
    @GetMapping("/user/{userId}")
    fun getAuditsByUser(@PathVariable userId: Long): List<Audit> = 
        auditService.getAuditsByUser(userId)
    
    @GetMapping("/date-range")
    fun getAuditsByDateRange(
        @RequestParam startDate: String,
        @RequestParam endDate: String
    ): List<Audit> {
        val start = LocalDateTime.parse(startDate)
        val end = LocalDateTime.parse(endDate)
        return auditService.getAuditsByDateRange(start, end)
    }
    
    @PostMapping("/{id}/assign/{userId}")
    fun assignAudit(@PathVariable id: Long, @PathVariable userId: Long): ResponseEntity<Audit> =
        auditService.assignAudit(id, userId)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    
    @PostMapping("/{id}/status/{status}")
    fun updateAuditStatus(@PathVariable id: Long, @PathVariable status: AuditStatus): ResponseEntity<Audit> =
        auditService.updateAuditStatus(id, status)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    
    @GetMapping("/search")
    fun searchAudits(@RequestParam query: String): List<Audit> = 
        auditService.searchAudits(query)
    
    @GetMapping("/overdue")
    fun getOverdueAudits(): List<Audit> = auditService.getOverdueAudits()
    
    @GetMapping("/upcoming")
    fun getUpcomingAudits(@RequestParam(defaultValue = "7") days: Int): List<Audit> = 
        auditService.getUpcomingAudits(days)
    
    @GetMapping("/tags")
    fun getAuditsByTags(@RequestParam tags: List<String>): List<Audit> = 
        auditService.getAuditsByTags(tags)
} 