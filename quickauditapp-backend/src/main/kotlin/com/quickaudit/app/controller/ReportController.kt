package com.quickaudit.app.controller

import com.quickaudit.app.model.Report
import com.quickaudit.app.repository.ReportRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reports")
class ReportController(private val reportRepository: ReportRepository) {
    @GetMapping
    fun getAllReports(): List<Report> = reportRepository.findAll()

    @GetMapping("/audit/{auditId}")
    fun getReportsByAudit(@PathVariable auditId: Long): List<Report> = reportRepository.findByAuditId(auditId)

    @GetMapping("/{id}")
    fun getReportById(@PathVariable id: Long): ResponseEntity<Report> =
        reportRepository.findById(id).map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())

    @PostMapping
    fun createReport(@RequestBody report: Report): Report = reportRepository.save(report)

    @PutMapping("/{id}")
    fun updateReport(@PathVariable id: Long, @RequestBody updated: Report): ResponseEntity<Report> {
        val existing = reportRepository.findById(id)
        return if (existing.isPresent) {
            val toUpdate = existing.get().copy(
                auditId = updated.auditId,
                createdAt = updated.createdAt,
                imageUrl = updated.imageUrl,
                gpsLocation = updated.gpsLocation,
                pdfUrl = updated.pdfUrl
            )
            ResponseEntity.ok(reportRepository.save(toUpdate))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteReport(@PathVariable id: Long): ResponseEntity<Void> {
        val existing = reportRepository.findById(id)
        return if (existing.isPresent) {
            reportRepository.delete(existing.get())
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
} 