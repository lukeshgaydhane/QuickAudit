package com.quickaudit.app.controller

import com.quickaudit.app.model.Audit
import com.quickaudit.app.repository.AuditRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/audits")
class AuditController(private val auditRepository: AuditRepository) {
    @GetMapping
    fun getAllAudits(): List<Audit> = auditRepository.findAll()

    @GetMapping("/{id}")
    fun getAuditById(@PathVariable id: Long): ResponseEntity<Audit> =
        auditRepository.findById(id).map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())

    @PostMapping
    fun createAudit(@RequestBody audit: Audit): Audit = auditRepository.save(audit)

    @PutMapping("/{id}")
    fun updateAudit(@PathVariable id: Long, @RequestBody updated: Audit): ResponseEntity<Audit> {
        val existing = auditRepository.findById(id)
        return if (existing.isPresent) {
            val toUpdate = existing.get().copy(
                title = updated.title,
                description = updated.description,
                date = updated.date,
                tags = updated.tags
            )
            ResponseEntity.ok(auditRepository.save(toUpdate))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteAudit(@PathVariable id: Long): ResponseEntity<Void> {
        val existing = auditRepository.findById(id)
        return if (existing.isPresent) {
            auditRepository.delete(existing.get())
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 