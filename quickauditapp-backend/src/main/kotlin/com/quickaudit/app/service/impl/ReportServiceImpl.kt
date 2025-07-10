package com.quickaudit.app.service.impl

import com.quickaudit.app.model.Report
import com.quickaudit.app.model.ReportStatus
import com.quickaudit.app.model.ReportSeverity
import com.quickaudit.app.repository.ReportRepository
import com.quickaudit.app.repository.AuditRepository
import com.quickaudit.app.repository.UserRepository
import com.quickaudit.app.service.ReportService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ReportServiceImpl(
    private val reportRepository: ReportRepository,
    private val auditRepository: AuditRepository,
    private val userRepository: UserRepository
) : ReportService {
    
    override fun getAllReports(): List<Report> = reportRepository.findAll()
    
    override fun getReportById(id: Long): Report? = reportRepository.findById(id).orElse(null)
    
    override fun createReport(report: Report): Report {
        // Validate that the audit exists
        auditRepository.findById(report.audit.id).orElseThrow {
            IllegalArgumentException("Audit not found")
        }
        
        // Validate that the generatedBy user exists
        userRepository.findById(report.generatedBy.id).orElseThrow {
            IllegalArgumentException("User not found")
        }
        
        return reportRepository.save(report)
    }
    
    override fun updateReport(id: Long, report: Report): Report? {
        val existingReport = getReportById(id) ?: return null
        
        val updatedReport = existingReport.copy(
            audit = report.audit,
            imageUrl = report.imageUrl,
            gpsLocation = report.gpsLocation,
            pdfUrl = report.pdfUrl,
            status = report.status,
            summary = report.summary,
            findings = report.findings,
            recommendations = report.recommendations,
            severity = report.severity,
            additionalFiles = report.additionalFiles,
            exportFormat = report.exportFormat,
            exportUrl = report.exportUrl,
            updatedAt = LocalDateTime.now()
        )
        
        return reportRepository.save(updatedReport)
    }
    
    override fun deleteReport(id: Long): Boolean {
        val report = getReportById(id) ?: return false
        reportRepository.delete(report)
        return true
    }
    
    override fun getReportsByAudit(auditId: Long): List<Report> = 
        reportRepository.findByAuditId(auditId)
    
    override fun getReportsByStatus(status: ReportStatus): List<Report> = 
        reportRepository.findByStatus(status)
    
    override fun getReportsBySeverity(severity: ReportSeverity): List<Report> = 
        reportRepository.findBySeverity(severity)
    
    override fun getReportsByUser(userId: Long): List<Report> = 
        reportRepository.findByGeneratedById(userId)
    
    override fun getReportsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<Report> = 
        reportRepository.findByCreatedAtBetween(startDate, endDate)
    
    override fun generatePDF(reportId: Long): String? {
        val report = getReportById(reportId) ?: return null
        
        // Simulate PDF generation - in a real implementation, you would use a PDF library
        val pdfUrl = "/reports/${reportId}/report.pdf"
        
        val updatedReport = report.copy(
            pdfUrl = pdfUrl,
            generatedAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        reportRepository.save(updatedReport)
        return pdfUrl
    }
    
    override fun exportReport(reportId: Long, format: String): String? {
        val report = getReportById(reportId) ?: return null
        
        // Simulate report export - in a real implementation, you would use appropriate libraries
        val exportUrl = "/reports/${reportId}/export.${format.lowercase()}"
        
        val updatedReport = report.copy(
            exportFormat = format,
            exportUrl = exportUrl,
            exportedAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        reportRepository.save(updatedReport)
        return exportUrl
    }
    
    override fun updateReportStatus(reportId: Long, status: ReportStatus): Report? {
        val report = getReportById(reportId) ?: return null
        
        val updatedReport = report.copy(
            status = status,
            updatedAt = LocalDateTime.now()
        )
        
        return reportRepository.save(updatedReport)
    }
    
    override fun addImageToReport(reportId: Long, imageUrl: String): Report? {
        val report = getReportById(reportId) ?: return null
        
        val updatedReport = report.copy(
            imageUrl = imageUrl,
            updatedAt = LocalDateTime.now()
        )
        
        return reportRepository.save(updatedReport)
    }
    
    override fun updateGPSLocation(reportId: Long, latitude: Double, longitude: Double): Report? {
        val report = getReportById(reportId) ?: return null
        
        val gpsLocation = "$latitude,$longitude"
        val updatedReport = report.copy(
            gpsLocation = gpsLocation,
            updatedAt = LocalDateTime.now()
        )
        
        return reportRepository.save(updatedReport)
    }
    
    override fun searchReports(query: String): List<Report> = 
        reportRepository.findBySummaryContainingIgnoreCaseOrFindingsContainingIgnoreCase(query, query)
    
    override fun getReportsWithImages(): List<Report> = 
        reportRepository.findByImageUrlIsNotNull()
    
    override fun getReportsWithGPS(): List<Report> = 
        reportRepository.findByGpsLocationIsNotNull()
    
    override fun approveReport(reportId: Long): Report? {
        val report = getReportById(reportId) ?: return null
        
        val updatedReport = report.copy(
            status = ReportStatus.APPROVED,
            updatedAt = LocalDateTime.now()
        )
        
        return reportRepository.save(updatedReport)
    }
    
    override fun publishReport(reportId: Long): Report? {
        val report = getReportById(reportId) ?: return null
        
        val updatedReport = report.copy(
            status = ReportStatus.PUBLISHED,
            updatedAt = LocalDateTime.now()
        )
        
        return reportRepository.save(updatedReport)
    }
} 