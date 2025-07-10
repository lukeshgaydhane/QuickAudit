package com.quickaudit.app.security

import com.quickaudit.app.model.User
import com.quickaudit.app.model.UserRole
import com.quickaudit.app.service.UserService
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userService: UserService) : UserDetailsService {
    
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userService.getUserByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        
        if (!user.isActive) {
            throw UsernameNotFoundException("User is inactive: $username")
        }
        
        val authorities = mutableListOf<SimpleGrantedAuthority>()
        
        // Add role-based authority
        authorities.add(SimpleGrantedAuthority("ROLE_${user.role.name}"))
        
        // Add specific permissions based on role
        when (user.role) {
            UserRole.ADMIN -> {
                authorities.add(SimpleGrantedAuthority("USER_CREATE"))
                authorities.add(SimpleGrantedAuthority("USER_READ"))
                authorities.add(SimpleGrantedAuthority("USER_UPDATE"))
                authorities.add(SimpleGrantedAuthority("USER_DELETE"))
                authorities.add(SimpleGrantedAuthority("AUDIT_CREATE"))
                authorities.add(SimpleGrantedAuthority("AUDIT_READ"))
                authorities.add(SimpleGrantedAuthority("AUDIT_UPDATE"))
                authorities.add(SimpleGrantedAuthority("AUDIT_DELETE"))
                authorities.add(SimpleGrantedAuthority("REPORT_CREATE"))
                authorities.add(SimpleGrantedAuthority("REPORT_READ"))
                authorities.add(SimpleGrantedAuthority("REPORT_UPDATE"))
                authorities.add(SimpleGrantedAuthority("REPORT_DELETE"))
            }
            UserRole.MANAGER -> {
                authorities.add(SimpleGrantedAuthority("USER_READ"))
                authorities.add(SimpleGrantedAuthority("AUDIT_CREATE"))
                authorities.add(SimpleGrantedAuthority("AUDIT_READ"))
                authorities.add(SimpleGrantedAuthority("AUDIT_UPDATE"))
                authorities.add(SimpleGrantedAuthority("REPORT_CREATE"))
                authorities.add(SimpleGrantedAuthority("REPORT_READ"))
                authorities.add(SimpleGrantedAuthority("REPORT_UPDATE"))
            }
            UserRole.AUDITOR -> {
                authorities.add(SimpleGrantedAuthority("AUDIT_CREATE"))
                authorities.add(SimpleGrantedAuthority("AUDIT_READ"))
                authorities.add(SimpleGrantedAuthority("AUDIT_UPDATE"))
                authorities.add(SimpleGrantedAuthority("REPORT_CREATE"))
                authorities.add(SimpleGrantedAuthority("REPORT_READ"))
                authorities.add(SimpleGrantedAuthority("REPORT_UPDATE"))
            }
            UserRole.USER -> {
                authorities.add(SimpleGrantedAuthority("AUDIT_READ"))
                authorities.add(SimpleGrantedAuthority("REPORT_READ"))
            }
        }
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.username)
            .password(user.password)
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(!user.isActive)
            .credentialsExpired(false)
            .disabled(!user.isActive)
            .build()
    }
} 