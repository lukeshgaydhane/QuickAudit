package com.quickaudit.app.service

import com.quickaudit.app.model.Report
import com.quickaudit.app.model.ReportStatus
import com.quickaudit.app.model.ReportSeverity
import java.time.LocalDateTime

interface ReportService {
    fun getAllReports(): List<Report>
    fun getReportById(id: Long): Report?
    fun createReport(report: Report): Report
    fun updateReport(id: Long, report: Report): Report?
    fun deleteReport(id: Long): Boolean
    fun getReportsByAudit(auditId: Long): List<Report>
    fun getReportsByStatus(status: ReportStatus): List<Report>
    fun getReportsBySeverity(severity: ReportSeverity): List<Report>
    fun getReportsByUser(userId: Long): List<Report>
    fun getReportsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<Report>
    fun generatePDF(reportId: Long): String? // Returns PDF URL
    fun exportReport(reportId: Long, format: String): String? // Returns export URL
    fun updateReportStatus(reportId: Long, status: ReportStatus): Report?
    fun addImageToReport(reportId: Long, imageUrl: String): Report?
    fun updateGPSLocation(reportId: Long, latitude: Double, longitude: Double): Report?
    fun searchReports(query: String): List<Report>
    fun getReportsWithImages(): List<Report>
    fun getReportsWithGPS(): List<Report>
    fun approveReport(reportId: Long): Report?
    fun publishReport(reportId: Long): Report?
} 