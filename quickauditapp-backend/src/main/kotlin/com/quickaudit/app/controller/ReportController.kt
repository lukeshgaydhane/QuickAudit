package com.quickaudit.app.controller

import com.quickaudit.app.model.Report
import com.quickaudit.app.model.ReportStatus
import com.quickaudit.app.model.ReportSeverity
import com.quickaudit.app.service.ReportService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/reports")
class ReportController(private val reportService: ReportService) {
    
    @GetMapping
    fun getAllReports(): List<Report> = reportService.getAllReports()
    
    @GetMapping("/audit/{auditId}")
    fun getReportsByAudit(@PathVariable auditId: Long): List<Report> = 
        reportService.getReportsByAudit(auditId)
    
    @GetMapping("/{id}")
    fun getReportById(@PathVariable id: Long): ResponseEntity<Report> =
        reportService.getReportById(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    
    @PostMapping
    fun createReport(@Valid @RequestBody report: Report): ResponseEntity<Report> {
        return try {
            val createdReport = reportService.createReport(report)
            ResponseEntity.ok(createdReport)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @PutMapping("/{id}")
    fun updateReport(@PathVariable id: Long, @Valid @RequestBody report: Report): ResponseEntity<Report> {
        return try {
            val updatedReport = reportService.updateReport(id, report)
            updatedReport?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @DeleteMapping("/{id}")
    fun deleteReport(@PathVariable id: Long): ResponseEntity<Void> {
        return if (reportService.deleteReport(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/status/{status}")
    fun getReportsByStatus(@PathVariable status: ReportStatus): List<Report> = 
        reportService.getReportsByStatus(status)
    
    @GetMapping("/severity/{severity}")
    fun getReportsBySeverity(@PathVariable severity: ReportSeverity): List<Report> = 
        reportService.getReportsBySeverity(severity)
    
    @GetMapping("/user/{userId}")
    fun getReportsByUser(@PathVariable userId: Long): List<Report> = 
        reportService.getReportsByUser(userId)
    
    @GetMapping("/date-range")
    fun getReportsByDateRange(
        @RequestParam startDate: String,
        @RequestParam endDate: String
    ): List<Report> {
        val start = LocalDateTime.parse(startDate)
        val end = LocalDateTime.parse(endDate)
        return reportService.getReportsByDateRange(start, end)
    }
    
    @PostMapping("/{id}/generate-pdf")
    fun generatePDF(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        val pdfUrl = reportService.generatePDF(id)
        return pdfUrl?.let { 
            ResponseEntity.ok(mapOf("pdfUrl" to it))
        } ?: ResponseEntity.notFound().build()
    }
    
    @PostMapping("/{id}/export")
    fun exportReport(
        @PathVariable id: Long,
        @RequestParam format: String
    ): ResponseEntity<Map<String, String>> {
        val exportUrl = reportService.exportReport(id, format)
        return exportUrl?.let { 
            ResponseEntity.ok(mapOf("exportUrl" to it))
        } ?: ResponseEntity.notFound().build()
    }
    
    @PostMapping("/{id}/status/{status}")
    fun updateReportStatus(@PathVariable id: Long, @PathVariable status: ReportStatus): ResponseEntity<Report> =
        reportService.updateReportStatus(id, status)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    
    @PostMapping("/{id}/image")
    fun addImageToReport(
        @PathVariable id: Long,
        @RequestBody imageData: Map<String, String>
    ): ResponseEntity<Report> {
        val imageUrl = imageData["imageUrl"] ?: return ResponseEntity.badRequest().build()
        return reportService.addImageToReport(id, imageUrl)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }
    
    @PostMapping("/{id}/gps")
    fun updateGPSLocation(
        @PathVariable id: Long,
        @RequestBody gpsData: Map<String, Double>
    ): ResponseEntity<Report> {
        val latitude = gpsData["latitude"] ?: return ResponseEntity.badRequest().build()
        val longitude = gpsData["longitude"] ?: return ResponseEntity.badRequest().build()
        
        return reportService.updateGPSLocation(id, latitude, longitude)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }
    
    @GetMapping("/search")
    fun searchReports(@RequestParam query: String): List<Report> = 
        reportService.searchReports(query)
    
    @GetMapping("/with-images")
    fun getReportsWithImages(): List<Report> = reportService.getReportsWithImages()
    
    @GetMapping("/with-gps")
    fun getReportsWithGPS(): List<Report> = reportService.getReportsWithGPS()
    
    @PostMapping("/{id}/approve")
    fun approveReport(@PathVariable id: Long): ResponseEntity<Report> =
        reportService.approveReport(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    
    @PostMapping("/{id}/publish")
    fun publishReport(@PathVariable id: Long): ResponseEntity<Report> =
        reportService.publishReport(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
} 