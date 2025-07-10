package com.quickaudit.app.security

import com.quickaudit.app.model.UserRole
import com.quickaudit.app.service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService,
    private val userService: UserService
) : OncePerRequestFilter() {
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }
        
        val jwt = authHeader.substring(7)
        val username = jwtUtil.extractUsername(jwt)
        
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(username)
            
            if (jwtUtil.validateToken(jwt, userDetails)) {
                val userInfo = jwtUtil.extractUserInfo(jwt)
                val authorities = mutableListOf<SimpleGrantedAuthority>()
                
                // Add role-based authority
                userInfo?.role?.let { role ->
                    authorities.add(SimpleGrantedAuthority("ROLE_${role.name}"))
                }
                
                // Add specific permissions based on role
                when (userInfo?.role) {
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
                    null -> {
                        // No specific permissions for null role
                    }
                }
                
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    authorities
                )
                
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }
        
        filterChain.doFilter(request, response)
    }
} 