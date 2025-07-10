package com.quickaudit.app.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import jakarta.validation.Validation
import jakarta.validation.Validator
import java.time.LocalDateTime

class ReportTest {
    
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator
    
    private val testUser = User(
        username = "testuser",
        password = "password123",
        email = "test@example.com"
    )
    
    private val testAudit = Audit(
        title = "Test Audit",
        createdBy = testUser
    )
    
    @Test
    fun `test valid report creation`() {
        val report = Report(
            audit = testAudit,
            generatedBy = testUser,
            status = ReportStatus.DRAFT,
            severity = ReportSeverity.MEDIUM
        )
        
        val violations = validator.validate(report)
        assertTrue(violations.isEmpty(), "Report should be valid")
        assertEquals(testAudit, report.audit)
        assertEquals(testUser, report.generatedBy)
        assertEquals(ReportStatus.DRAFT, report.status)
        assertEquals(ReportSeverity.MEDIUM, report.severity)
        assertNotNull(report.createdAt)
    }
    
    @Test
    fun `test report with minimum required fields`() {
        val report = Report(
            audit = testAudit,
            generatedBy = testUser
        )
        
        val violations = validator.validate(report)
        assertTrue(violations.isEmpty(), "Report with minimum fields should be valid")
        assertEquals(testAudit, report.audit)
        assertEquals(testUser, report.generatedBy)
        assertEquals(ReportStatus.DRAFT, report.status)
        assertEquals(ReportSeverity.MEDIUM, report.severity)
    }
    
    @Test
    fun `test report status transitions`() {
        val statuses = ReportStatus.values()
        
        statuses.forEach { status ->
            val report = Report(
                audit = testAudit,
                generatedBy = testUser,
                status = status
            )
            
            assertEquals(status, report.status)
        }
    }
    
    @Test
    fun `test report severity levels`() {
        val severities = ReportSeverity.values()
        
        severities.forEach { severity ->
            val report = Report(
                audit = testAudit,
                generatedBy = testUser,
                severity = severity
            )
            
            assertEquals(severity, report.severity)
        }
    }
    
    @Test
    fun `test report with GPS coordinates`() {
        val report = Report(
            audit = testAudit,
            generatedBy = testUser,
            gpsLocation = "40.7128,-74.0060"
        )
        
        val violations = validator.validate(report)
        assertTrue(violations.isEmpty(), "Report with GPS coordinates should be valid")
        assertEquals("40.7128,-74.0060", report.gpsLocation)
    }
    
    @Test
    fun `test report with image URL`() {
        val report = Report(
            audit = testAudit,
            generatedBy = testUser,
            imageUrl = "https://example.com/audit-image.jpg"
        )
        
        val violations = validator.validate(report)
        assertTrue(violations.isEmpty(), "Report with image URL should be valid")
        assertEquals("https://example.com/audit-image.jpg", report.imageUrl)
    }
    
    @Test
    fun `test report with PDF URL`() {
        val report = Report(
            audit = testAudit,
            generatedBy = testUser,
            pdfUrl = "https://example.com/report.pdf"
        )
        
        val violations = validator.validate(report)
        assertTrue(violations.isEmpty(), "Report with PDF URL should be valid")
        assertEquals("https://example.com/report.pdf", report.pdfUrl)
    }
    
    @Test
    fun `test report with summary and findings`() {
        val summary = "This is a summary of the audit findings"
        val findings = "Detailed findings from the audit process including compliance issues and recommendations"
        
        val report = Report(
            audit = testAudit,
            generatedBy = testUser,
            summary = summary,
            findings = findings
        )
        
        val violations = validator.validate(report)
        assertTrue(violations.isEmpty(), "Report with summary and findings should be valid")
        assertEquals(summary, report.summary)
        assertEquals(findings, report.findings)
    }
    
    @Test
    fun `test report with recommendations`() {
        val recommendations = "1. Implement new safety protocols\n2. Update training materials\n3. Schedule follow-up audit"
        
        val report = Report(
            audit = testAudit,
            generatedBy = testUser,
            recommendations = recommendations
        )
        
        assertEquals(recommendations, report.recommendations)
    }
    
    @Test
    fun `test report with additional files`() {
        val additionalFiles = "file1.pdf,file2.jpg,file3.docx"
        
        val report = Report(
            audit = testAudit,
            generatedBy = testUser,
            additionalFiles = additionalFiles
        )
        
        assertEquals(additionalFiles, report.additionalFiles)
    }
    
    @Test
    fun `test report export functionality`() {
        val report = Report(
            audit = testAudit,
            generatedBy = testUser,
            exportFormat = "PDF",
            exportUrl = "https://example.com/export.pdf"
        )
        
        assertEquals("PDF", report.exportFormat)
        assertEquals("https://example.com/export.pdf", report.exportUrl)
    }
    
    @Test
    fun `test report copy with modifications`() {
        val originalReport = Report(
            audit = testAudit,
            generatedBy = testUser,
            status = ReportStatus.DRAFT,
            summary = "Original summary"
        )
        
        val modifiedReport = originalReport.copy(
            status = ReportStatus.APPROVED,
            summary = "Modified summary"
        )
        
        assertEquals(ReportStatus.APPROVED, modifiedReport.status)
        assertEquals("Modified summary", modifiedReport.summary)
        assertEquals(testAudit, modifiedReport.audit) // unchanged
        assertEquals(testUser, modifiedReport.generatedBy) // unchanged
        assertEquals(originalReport.id, modifiedReport.id) // unchanged
    }
    
    @Test
    fun `test report with generation timestamps`() {
        val generatedAt = LocalDateTime.now()
        val exportedAt = LocalDateTime.now().plusHours(1)
        
        val report = Report(
            audit = testAudit,
            generatedBy = testUser,
            generatedAt = generatedAt,
            exportedAt = exportedAt
        )
        
        assertEquals(generatedAt, report.generatedAt)
        assertEquals(exportedAt, report.exportedAt)
    }
    
    @Test
    fun `test report with all fields`() {
        val generatedAt = LocalDateTime.now()
        val exportedAt = LocalDateTime.now().plusHours(1)
        
        val report = Report(
            audit = testAudit,
            generatedBy = testUser,
            status = ReportStatus.PUBLISHED,
            severity = ReportSeverity.HIGH,
            summary = "Comprehensive audit summary",
            findings = "Detailed findings from the audit process",
            recommendations = "Action items and recommendations",
            imageUrl = "https://example.com/audit-image.jpg",
            gpsLocation = "40.7128,-74.0060",
            pdfUrl = "https://example.com/report.pdf",
            additionalFiles = "file1.pdf,file2.jpg",
            generatedAt = generatedAt,
            exportedAt = exportedAt,
            exportFormat = "PDF",
            exportUrl = "https://example.com/export.pdf"
        )
        
        val violations = validator.validate(report)
        assertTrue(violations.isEmpty(), "Complete report should be valid")
        assertEquals(testAudit, report.audit)
        assertEquals(testUser, report.generatedBy)
        assertEquals(ReportStatus.PUBLISHED, report.status)
        assertEquals(ReportSeverity.HIGH, report.severity)
        assertEquals("Comprehensive audit summary", report.summary)
        assertEquals("Detailed findings from the audit process", report.findings)
        assertEquals("Action items and recommendations", report.recommendations)
        assertEquals("https://example.com/audit-image.jpg", report.imageUrl)
        assertEquals("40.7128,-74.0060", report.gpsLocation)
        assertEquals("https://example.com/report.pdf", report.pdfUrl)
        assertEquals("file1.pdf,file2.jpg", report.additionalFiles)
        assertEquals(generatedAt, report.generatedAt)
        assertEquals(exportedAt, report.exportedAt)
        assertEquals("PDF", report.exportFormat)
        assertEquals("https://example.com/export.pdf", report.exportUrl)
    }
    
    @Test
    fun `test GPS coordinate parsing`() {
        val latitude = 40.7128
        val longitude = -74.0060
        val gpsLocation = "$latitude,$longitude"
        
        val report = Report(
            audit = testAudit,
            generatedBy = testUser,
            gpsLocation = gpsLocation
        )
        
        assertEquals(gpsLocation, report.gpsLocation)
        
        // Test parsing the coordinates back
        val coordinates = report.gpsLocation?.split(",")
        assertNotNull(coordinates)
        assertEquals(2, coordinates?.size)
        assertEquals(latitude.toString(), coordinates?.get(0))
        assertEquals(longitude.toString(), coordinates?.get(1))
    }
    
    @Test
    fun `test report approval workflow`() {
        val report = Report(
            audit = testAudit,
            generatedBy = testUser,
            status = ReportStatus.DRAFT
        )
        
        // Simulate approval workflow
        val approvedReport = report.copy(status = ReportStatus.APPROVED)
        assertEquals(ReportStatus.APPROVED, approvedReport.status)
        
        val publishedReport = approvedReport.copy(status = ReportStatus.PUBLISHED)
        assertEquals(ReportStatus.PUBLISHED, publishedReport.status)
    }
} 