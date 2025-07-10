package com.quickaudit.app.config

import com.quickaudit.app.security.CustomUserDetailsService
import com.quickaudit.app.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.http.HttpMethod
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
open class SecurityConfig {
    
    @Autowired
    private lateinit var applicationContext: ApplicationContext
    
    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests { auth ->
                auth
                    // Public endpoints
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    
                    // User management - Admin only
                    .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "MANAGER")
                    .requestMatchers("/api/users").hasRole("ADMIN")
                    .requestMatchers("/api/users/{id}").hasRole("ADMIN")
                    .requestMatchers("/api/users/{id}/activate").hasRole("ADMIN")
                    .requestMatchers("/api/users/{id}/deactivate").hasRole("ADMIN")
                    
                    // Audit endpoints
                    .requestMatchers(HttpMethod.GET, "/api/audits/**").hasAnyRole("ADMIN", "MANAGER", "AUDITOR", "USER")
                    .requestMatchers(HttpMethod.POST, "/api/audits/**").hasAnyRole("ADMIN", "MANAGER", "AUDITOR")
                    .requestMatchers(HttpMethod.PUT, "/api/audits/**").hasAnyRole("ADMIN", "MANAGER", "AUDITOR")
                    .requestMatchers(HttpMethod.DELETE, "/api/audits/**").hasAnyRole("ADMIN", "MANAGER")
                    
                    // Report endpoints
                    .requestMatchers(HttpMethod.GET, "/api/reports/**").hasAnyRole("ADMIN", "MANAGER", "AUDITOR", "USER")
                    .requestMatchers(HttpMethod.POST, "/api/reports/**").hasAnyRole("ADMIN", "MANAGER", "AUDITOR")
                    .requestMatchers(HttpMethod.PUT, "/api/reports/**").hasAnyRole("ADMIN", "MANAGER", "AUDITOR")
                    .requestMatchers(HttpMethod.DELETE, "/api/reports/**").hasAnyRole("ADMIN", "MANAGER")
                    
                    // All other requests require authentication
                    .anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(applicationContext.getBean(JwtAuthenticationFilter::class.java), UsernamePasswordAuthenticationFilter::class.java)
        
        return http.build()
    }
    
    @Bean
    open fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
    
    @Bean
    open fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(applicationContext.getBean(CustomUserDetailsService::class.java))
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }
    
    @Bean
    open fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }
    
    @Bean
    open fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
} 