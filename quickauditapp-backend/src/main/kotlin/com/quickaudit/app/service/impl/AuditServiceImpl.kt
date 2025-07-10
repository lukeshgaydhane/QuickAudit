package com.quickaudit.app.service.impl

import com.quickaudit.app.model.Audit
import com.quickaudit.app.model.AuditStatus
import com.quickaudit.app.model.AuditPriority
import com.quickaudit.app.repository.AuditRepository
import com.quickaudit.app.repository.UserRepository
import com.quickaudit.app.service.AuditService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuditServiceImpl(
    private val auditRepository: AuditRepository,
    private val userRepository: UserRepository
) : AuditService {
    
    override fun getAllAudits(): List<Audit> = auditRepository.findAll()
    
    override fun getAuditById(id: Long): Audit? = auditRepository.findById(id).orElse(null)
    
    override fun createAudit(audit: Audit): Audit {
        // Validate that the createdBy user exists
        userRepository.findById(audit.createdBy.id).orElseThrow {
            IllegalArgumentException("User not found")
        }
        
        // Validate that assignedTo user exists if provided
        audit.assignedTo?.let { assignedUser ->
            userRepository.findById(assignedUser.id).orElseThrow {
                IllegalArgumentException("Assigned user not found")
            }
        }
        
        return auditRepository.save(audit)
    }
    
    override fun updateAudit(id: Long, audit: Audit): Audit? {
        val existingAudit = getAuditById(id) ?: return null
        
        // Validate that assignedTo user exists if provided
        audit.assignedTo?.let { assignedUser ->
            userRepository.findById(assignedUser.id).orElseThrow {
                IllegalArgumentException("Assigned user not found")
            }
        }
        
        val updatedAudit = existingAudit.copy(
            title = audit.title,
            description = audit.description,
            auditDate = audit.auditDate,
            tags = audit.tags,
            status = audit.status,
            assignedTo = audit.assignedTo,
            location = audit.location,
            department = audit.department,
            priority = audit.priority,
            notes = audit.notes,
            scheduledDate = audit.scheduledDate,
            completedDate = audit.completedDate,
            updatedAt = LocalDateTime.now()
        )
        
        return auditRepository.save(updatedAudit)
    }
    
    override fun deleteAudit(id: Long): Boolean {
        val audit = getAuditById(id) ?: return false
        auditRepository.delete(audit)
        return true
    }
    
    override fun getAuditsByStatus(status: AuditStatus): List<Audit> = 
        auditRepository.findByStatus(status)
    
    override fun getAuditsByPriority(priority: AuditPriority): List<Audit> = 
        auditRepository.findByPriority(priority)
    
    override fun getAuditsByUser(userId: Long): List<Audit> = 
        auditRepository.findByCreatedByIdOrAssignedToId(userId, userId)
    
    override fun getAuditsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<Audit> = 
        auditRepository.findByAuditDateBetween(startDate, endDate)
    
    override fun assignAudit(auditId: Long, userId: Long): Audit? {
        val audit = getAuditById(auditId) ?: return null
        val user = userRepository.findById(userId).orElse(null) ?: return null
        
        val updatedAudit = audit.copy(
            assignedTo = user,
            updatedAt = LocalDateTime.now()
        )
        
        return auditRepository.save(updatedAudit)
    }
    
    override fun updateAuditStatus(auditId: Long, status: AuditStatus): Audit? {
        val audit = getAuditById(auditId) ?: return null
        
        val updatedAudit = audit.copy(
            status = status,
            updatedAt = LocalDateTime.now(),
            completedDate = if (status == AuditStatus.COMPLETED) LocalDateTime.now() else audit.completedDate
        )
        
        return auditRepository.save(updatedAudit)
    }
    
    override fun getAuditsByTags(tags: List<String>): List<Audit> {
        val tagString = tags.joinToString(",")
        return auditRepository.findByTagsContaining(tagString)
    }
    
    override fun searchAudits(query: String): List<Audit> = 
        auditRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query)
    
    override fun getOverdueAudits(): List<Audit> {
        val now = LocalDateTime.now()
        return auditRepository.findByScheduledDateBeforeAndStatusNot(now, AuditStatus.COMPLETED)
    }
    
    override fun getUpcomingAudits(days: Int): List<Audit> {
        val startDate = LocalDateTime.now()
        val endDate = startDate.plusDays(days.toLong())
        return auditRepository.findByScheduledDateBetweenAndStatusNot(startDate, endDate, AuditStatus.COMPLETED)
    }
} 