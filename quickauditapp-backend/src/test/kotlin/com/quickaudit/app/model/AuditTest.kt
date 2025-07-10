package com.quickaudit.app.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import jakarta.validation.Validation
import jakarta.validation.Validator
import java.time.LocalDateTime

class AuditTest {
    
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator
    
    private val testUser = User(
        username = "testuser",
        password = "password123",
        email = "test@example.com"
    )
    
    @Test
    fun `test valid audit creation`() {
        val audit = Audit(
            title = "Test Audit",
            description = "A test audit for validation",
            createdBy = testUser,
            status = AuditStatus.DRAFT,
            priority = AuditPriority.MEDIUM
        )
        
        val violations = validator.validate(audit)
        assertTrue(violations.isEmpty(), "Audit should be valid")
        assertEquals("Test Audit", audit.title)
        assertEquals("A test audit for validation", audit.description)
        assertEquals(testUser, audit.createdBy)
        assertEquals(AuditStatus.DRAFT, audit.status)
        assertEquals(AuditPriority.MEDIUM, audit.priority)
        assertNotNull(audit.auditDate)
        assertNotNull(audit.createdAt)
    }
    
    @Test
    fun `test audit with minimum required fields`() {
        val audit = Audit(
            title = "Minimal Audit",
            createdBy = testUser
        )
        
        val violations = validator.validate(audit)
        assertTrue(violations.isEmpty(), "Audit with minimum fields should be valid")
        assertEquals("Minimal Audit", audit.title)
        assertNull(audit.description)
        assertEquals(testUser, audit.createdBy)
        assertEquals(AuditStatus.DRAFT, audit.status)
        assertEquals(AuditPriority.MEDIUM, audit.priority)
    }
    
    @Test
    fun `test invalid title - too long`() {
        val audit = Audit(
            title = "a".repeat(201), // too long
            createdBy = testUser
        )
        
        val violations = validator.validate(audit)
        assertFalse(violations.isEmpty(), "Audit with long title should be invalid")
        assertTrue(violations.any { it.propertyPath.toString() == "title" })
    }
    
    @Test
    fun `test invalid title - empty`() {
        val audit = Audit(
            title = "", // empty
            createdBy = testUser
        )
        
        val violations = validator.validate(audit)
        assertFalse(violations.isEmpty(), "Audit with empty title should be invalid")
        assertTrue(violations.any { it.propertyPath.toString() == "title" })
    }
    
    @Test
    fun `test audit status transitions`() {
        val statuses = AuditStatus.values()
        
        statuses.forEach { status ->
            val audit = Audit(
                title = "Status Test Audit - ${status.name}",
                createdBy = testUser,
                status = status
            )
            
            assertEquals(status, audit.status)
        }
    }
    
    @Test
    fun `test audit priority levels`() {
        val priorities = AuditPriority.values()
        
        priorities.forEach { priority ->
            val audit = Audit(
                title = "Priority Test Audit - ${priority.name}",
                createdBy = testUser,
                priority = priority
            )
            
            assertEquals(priority, audit.priority)
        }
    }
    
    @Test
    fun `test audit with tags`() {
        val audit = Audit(
            title = "Tagged Audit",
            createdBy = testUser,
            tags = "safety,compliance,urgent"
        )
        
        val violations = validator.validate(audit)
        assertTrue(violations.isEmpty(), "Audit with tags should be valid")
        assertEquals("safety,compliance,urgent", audit.tags)
    }
    
    @Test
    fun `test audit with location and department`() {
        val audit = Audit(
            title = "Location Audit",
            createdBy = testUser,
            location = "Building A, Floor 3",
            department = "Operations"
        )
        
        val violations = validator.validate(audit)
        assertTrue(violations.isEmpty(), "Audit with location and department should be valid")
        assertEquals("Building A, Floor 3", audit.location)
        assertEquals("Operations", audit.department)
    }
    
    @Test
    fun `test audit assignment`() {
        val assignedUser = User(
            username = "assigneduser",
            password = "password123"
        )
        
        val audit = Audit(
            title = "Assigned Audit",
            createdBy = testUser,
            assignedTo = assignedUser
        )
        
        assertEquals(testUser, audit.createdBy)
        assertEquals(assignedUser, audit.assignedTo)
    }
    
    @Test
    fun `test audit with scheduled date`() {
        val scheduledDate = LocalDateTime.now().plusDays(7)
        val audit = Audit(
            title = "Scheduled Audit",
            createdBy = testUser,
            scheduledDate = scheduledDate
        )
        
        assertEquals(scheduledDate, audit.scheduledDate)
    }
    
    @Test
    fun `test audit copy with modifications`() {
        val originalAudit = Audit(
            title = "Original Audit",
            description = "Original description",
            createdBy = testUser,
            status = AuditStatus.DRAFT
        )
        
        val modifiedAudit = originalAudit.copy(
            title = "Modified Audit",
            description = "Modified description",
            status = AuditStatus.IN_PROGRESS
        )
        
        assertEquals("Modified Audit", modifiedAudit.title)
        assertEquals("Modified description", modifiedAudit.description)
        assertEquals(AuditStatus.IN_PROGRESS, modifiedAudit.status)
        assertEquals(testUser, modifiedAudit.createdBy) // unchanged
        assertEquals(originalAudit.id, modifiedAudit.id) // unchanged
    }
    
    @Test
    fun `test audit with notes`() {
        val notes = "This is a detailed note about the audit findings and observations."
        val audit = Audit(
            title = "Noted Audit",
            createdBy = testUser,
            notes = notes
        )
        
        assertEquals(notes, audit.notes)
    }
    
    @Test
    fun `test audit completion`() {
        val audit = Audit(
            title = "Completed Audit",
            createdBy = testUser,
            status = AuditStatus.COMPLETED,
            completedDate = LocalDateTime.now()
        )
        
        assertEquals(AuditStatus.COMPLETED, audit.status)
        assertNotNull(audit.completedDate)
    }
    
    @Test
    fun `test audit with all fields`() {
        val assignedUser = User(
            username = "assigneduser",
            password = "password123"
        )
        
        val scheduledDate = LocalDateTime.now().plusDays(5)
        val audit = Audit(
            title = "Complete Audit",
            description = "A comprehensive audit with all fields",
            createdBy = testUser,
            assignedTo = assignedUser,
            location = "Main Office",
            department = "Quality Assurance",
            priority = AuditPriority.HIGH,
            notes = "Important audit notes",
            scheduledDate = scheduledDate,
            tags = "quality,compliance,high-priority"
        )
        
        val violations = validator.validate(audit)
        assertTrue(violations.isEmpty(), "Complete audit should be valid")
        assertEquals("Complete Audit", audit.title)
        assertEquals("A comprehensive audit with all fields", audit.description)
        assertEquals(testUser, audit.createdBy)
        assertEquals(assignedUser, audit.assignedTo)
        assertEquals("Main Office", audit.location)
        assertEquals("Quality Assurance", audit.department)
        assertEquals(AuditPriority.HIGH, audit.priority)
        assertEquals("Important audit notes", audit.notes)
        assertEquals(scheduledDate, audit.scheduledDate)
        assertEquals("quality,compliance,high-priority", audit.tags)
    }
} 