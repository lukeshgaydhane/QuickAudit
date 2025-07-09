package com.quickaudit.app.controller

import com.quickaudit.app.model.AuditReport
import com.quickaudit.app.repository.AuditReportRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/audit-reports")
class AuditReportController(private val repository: AuditReportRepository) {

    @GetMapping
    fun getAll(): List<AuditReport> = repository.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<AuditReport> =
        repository.findById(id).map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())

    @PostMapping
    fun create(@RequestBody report: AuditReport): AuditReport = repository.save(report)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updated: AuditReport): ResponseEntity<AuditReport> {
        val existing = repository.findById(id)
        return if (existing.isPresent) {
            val toUpdate = existing.get().copy(
                title = updated.title,
                description = updated.description,
                date = updated.date
            )
            ResponseEntity.ok(repository.save(toUpdate))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val existing = repository.findById(id)
        return if (existing.isPresent) {
            repository.delete(existing.get())
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 