package com.quickaudit.app.model

import com.quickaudit.app.model.ChecklistItemStatus.*
import com.quickaudit.app.model.ChecklistItemType.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

class ChecklistItemTest {
    
    private val testUser = User(
        id = 1L,
        username = "testuser",
        password = "password123",
        email = "test@example.com",
        fullName = "Test User",
        role = UserRole.AUDITOR
    )
    
    private val testAudit = Audit(
        id = 1L,
        title = "Test Audit",
        description = "Test audit description",
        createdBy = testUser,
        location = "Test Location"
    )
    
    @Test
    fun `test create checklist item with minimal required fields`() {
        val checklistItem = ChecklistItem(
            audit = testAudit,
            question = "Is the equipment properly maintained?"
        )
        
        assertEquals(0L, checklistItem.id)
        assertEquals(testAudit, checklistItem.audit)
        assertEquals("Is the equipment properly maintained?", checklistItem.question)
        assertNull(checklistItem.answer)
        assertEquals(PENDING, checklistItem.status)
        assertEquals(YES_NO, checklistItem.type)
        assertEquals(0, checklistItem.orderIndex)
        assertTrue(checklistItem.isRequired)
        assertTrue(checklistItem.isActive)
        assertNotNull(checklistItem.createdAt)
        assertNull(checklistItem.updatedAt)
    }
    
    @Test
    fun `test create checklist item with all fields`() {
        val checklistItem = ChecklistItem(
            audit = testAudit,
            question = "Is the equipment properly maintained?",
            answer = "Yes, all equipment is maintained according to schedule",
            status = COMPLETED,
            type = TEXT,
            orderIndex = 1,
            notes = "Equipment maintenance records reviewed",
            category = "Safety",
            subcategory = "Equipment",
            isRequired = true,
            answeredBy = testUser,
            answeredAt = LocalDateTime.now(),
            evidence = "maintenance_logs.pdf",
            riskLevel = "Low",
            recommendations = "Continue current maintenance schedule",
            isActive = true
        )
        
        assertEquals(testAudit, checklistItem.audit)
        assertEquals("Is the equipment properly maintained?", checklistItem.question)
        assertEquals("Yes, all equipment is maintained according to schedule", checklistItem.answer)
        assertEquals(COMPLETED, checklistItem.status)
        assertEquals(TEXT, checklistItem.type)
        assertEquals(1, checklistItem.orderIndex)
        assertEquals("Equipment maintenance records reviewed", checklistItem.notes)
        assertEquals("Safety", checklistItem.category)
        assertEquals("Equipment", checklistItem.subcategory)
        assertTrue(checklistItem.isRequired)
        assertEquals(testUser, checklistItem.answeredBy)
        assertNotNull(checklistItem.answeredAt)
        assertEquals("maintenance_logs.pdf", checklistItem.evidence)
        assertEquals("Low", checklistItem.riskLevel)
        assertEquals("Continue current maintenance schedule", checklistItem.recommendations)
        assertTrue(checklistItem.isActive)
    }
    
    @Test
    fun `test checklist item with different types`() {
        val yesNoItem = ChecklistItem(
            audit = testAudit,
            question = "Is safety equipment available?",
            type = YES_NO
        )
        assertEquals(YES_NO, yesNoItem.type)
        
        val multipleChoiceItem = ChecklistItem(
            audit = testAudit,
            question = "What is the current safety rating?",
            type = MULTIPLE_CHOICE
        )
        assertEquals(MULTIPLE_CHOICE, multipleChoiceItem.type)
        
        val textItem = ChecklistItem(
            audit = testAudit,
            question = "Describe the current safety procedures",
            type = TEXT
        )
        assertEquals(TEXT, textItem.type)
        
        val numberItem = ChecklistItem(
            audit = testAudit,
            question = "How many safety incidents occurred?",
            type = NUMBER
        )
        assertEquals(NUMBER, numberItem.type)
        
        val dateItem = ChecklistItem(
            audit = testAudit,
            question = "When was the last safety inspection?",
            type = DATE
        )
        assertEquals(DATE, dateItem.type)
        
        val fileUploadItem = ChecklistItem(
            audit = testAudit,
            question = "Upload safety inspection report",
            type = FILE_UPLOAD
        )
        assertEquals(FILE_UPLOAD, fileUploadItem.type)
        
        val locationItem = ChecklistItem(
            audit = testAudit,
            question = "Where is the safety equipment located?",
            type = LOCATION
        )
        assertEquals(LOCATION, locationItem.type)
        
        val signatureItem = ChecklistItem(
            audit = testAudit,
            question = "Sign to confirm safety procedures reviewed",
            type = SIGNATURE
        )
        assertEquals(SIGNATURE, signatureItem.type)
    }
    
    @Test
    fun `test checklist item with different statuses`() {
        val pendingItem = ChecklistItem(
            audit = testAudit,
            question = "Pending question",
            status = PENDING
        )
        assertEquals(PENDING, pendingItem.status)
        
        val inProgressItem = ChecklistItem(
            audit = testAudit,
            question = "In progress question",
            status = IN_PROGRESS
        )
        assertEquals(IN_PROGRESS, inProgressItem.status)
        
        val completedItem = ChecklistItem(
            audit = testAudit,
            question = "Completed question",
            status = COMPLETED
        )
        assertEquals(COMPLETED, completedItem.status)
        
        val skippedItem = ChecklistItem(
            audit = testAudit,
            question = "Skipped question",
            status = SKIPPED
        )
        assertEquals(SKIPPED, skippedItem.status)
        
        val notApplicableItem = ChecklistItem(
            audit = testAudit,
            question = "Not applicable question",
            status = NOT_APPLICABLE
        )
        assertEquals(NOT_APPLICABLE, notApplicableItem.status)
    }
    
    @Test
    fun `test checklist item with optional fields`() {
        val optionalItem = ChecklistItem(
            audit = testAudit,
            question = "Optional question",
            isRequired = false
        )
        
        assertFalse(optionalItem.isRequired)
        assertNull(optionalItem.answer)
        assertNull(optionalItem.notes)
        assertNull(optionalItem.category)
        assertNull(optionalItem.subcategory)
        assertNull(optionalItem.answeredBy)
        assertNull(optionalItem.answeredAt)
        assertNull(optionalItem.evidence)
        assertNull(optionalItem.riskLevel)
        assertNull(optionalItem.recommendations)
    }
    
    @Test
    fun `test checklist item with inactive status`() {
        val inactiveItem = ChecklistItem(
            audit = testAudit,
            question = "Inactive question",
            isActive = false
        )
        
        assertFalse(inactiveItem.isActive)
    }
    
    @Test
    fun `test checklist item with long text fields`() {
        val longQuestion = "A".repeat(1000)
        val longAnswer = "B".repeat(2000)
        val longNotes = "C".repeat(500)
        val longEvidence = "D".repeat(1000)
        val longRecommendations = "E".repeat(500)
        
        val checklistItem = ChecklistItem(
            audit = testAudit,
            question = longQuestion,
            answer = longAnswer,
            notes = longNotes,
            evidence = longEvidence,
            recommendations = longRecommendations
        )
        
        assertEquals(longQuestion, checklistItem.question)
        assertEquals(longAnswer, checklistItem.answer)
        assertEquals(longNotes, checklistItem.notes)
        assertEquals(longEvidence, checklistItem.evidence)
        assertEquals(longRecommendations, checklistItem.recommendations)
    }
    
    @Test
    fun `test checklist item with category and subcategory`() {
        val checklistItem = ChecklistItem(
            audit = testAudit,
            question = "Category test question",
            category = "Safety",
            subcategory = "Equipment"
        )
        
        assertEquals("Safety", checklistItem.category)
        assertEquals("Equipment", checklistItem.subcategory)
    }
    
    @Test
    fun `test checklist item with risk level`() {
        val highRiskItem = ChecklistItem(
            audit = testAudit,
            question = "High risk question",
            riskLevel = "High"
        )
        assertEquals("High", highRiskItem.riskLevel)
        
        val mediumRiskItem = ChecklistItem(
            audit = testAudit,
            question = "Medium risk question",
            riskLevel = "Medium"
        )
        assertEquals("Medium", mediumRiskItem.riskLevel)
        
        val lowRiskItem = ChecklistItem(
            audit = testAudit,
            question = "Low risk question",
            riskLevel = "Low"
        )
        assertEquals("Low", lowRiskItem.riskLevel)
    }
    
    @Test
    fun `test checklist item with answered information`() {
        val answeredAt = LocalDateTime.now()
        val checklistItem = ChecklistItem(
            audit = testAudit,
            question = "Answered question",
            answer = "Yes, it is compliant",
            status = COMPLETED,
            answeredBy = testUser,
            answeredAt = answeredAt
        )
        
        assertEquals("Yes, it is compliant", checklistItem.answer)
        assertEquals(COMPLETED, checklistItem.status)
        assertEquals(testUser, checklistItem.answeredBy)
        assertEquals(answeredAt, checklistItem.answeredAt)
    }
    
    @Test
    fun `test checklist item with order index`() {
        val checklistItem = ChecklistItem(
            audit = testAudit,
            question = "Ordered question",
            orderIndex = 5
        )
        
        assertEquals(5, checklistItem.orderIndex)
    }
    
    @Test
    fun `test checklist item copy with modifications`() {
        val originalItem = ChecklistItem(
            audit = testAudit,
            question = "Original question",
            answer = "Original answer",
            status = PENDING
        )
        
        val modifiedItem = originalItem.copy(
            answer = "Modified answer",
            status = COMPLETED,
            answeredBy = testUser,
            answeredAt = LocalDateTime.now()
        )
        
        assertEquals(originalItem.audit, modifiedItem.audit)
        assertEquals(originalItem.question, modifiedItem.question)
        assertEquals("Modified answer", modifiedItem.answer)
        assertEquals(COMPLETED, modifiedItem.status)
        assertEquals(testUser, modifiedItem.answeredBy)
        assertNotNull(modifiedItem.answeredAt)
    }
    
    @Test
    fun `test checklist item with evidence`() {
        val checklistItem = ChecklistItem(
            audit = testAudit,
            question = "Evidence question",
            evidence = "safety_report.pdf, inspection_photos.jpg"
        )
        
        assertEquals("safety_report.pdf, inspection_photos.jpg", checklistItem.evidence)
    }
    
    @Test
    fun `test checklist item with recommendations`() {
        val checklistItem = ChecklistItem(
            audit = testAudit,
            question = "Recommendations question",
            recommendations = "Implement additional safety training and update procedures"
        )
        
        assertEquals("Implement additional safety training and update procedures", checklistItem.recommendations)
    }
    
    @Test
    fun `test checklist item with updated timestamp`() {
        val originalItem = ChecklistItem(
            audit = testAudit,
            question = "Timestamp test question"
        )
        
        val updatedAt = LocalDateTime.now()
        val updatedItem = originalItem.copy(updatedAt = updatedAt)
        
        assertEquals(updatedAt, updatedItem.updatedAt)
    }
    
    @Test
    fun `test checklist item enum values`() {
        // Test ChecklistItemStatus enum
        assertEquals(5, ChecklistItemStatus.values().size)
        assertTrue(ChecklistItemStatus.values().contains(PENDING))
        assertTrue(ChecklistItemStatus.values().contains(IN_PROGRESS))
        assertTrue(ChecklistItemStatus.values().contains(COMPLETED))
        assertTrue(ChecklistItemStatus.values().contains(SKIPPED))
        assertTrue(ChecklistItemStatus.values().contains(NOT_APPLICABLE))
        
        // Test ChecklistItemType enum
        assertEquals(8, ChecklistItemType.values().size)
        assertTrue(ChecklistItemType.values().contains(YES_NO))
        assertTrue(ChecklistItemType.values().contains(MULTIPLE_CHOICE))
        assertTrue(ChecklistItemType.values().contains(TEXT))
        assertTrue(ChecklistItemType.values().contains(NUMBER))
        assertTrue(ChecklistItemType.values().contains(DATE))
        assertTrue(ChecklistItemType.values().contains(FILE_UPLOAD))
        assertTrue(ChecklistItemType.values().contains(LOCATION))
        assertTrue(ChecklistItemType.values().contains(SIGNATURE))
    }
} 