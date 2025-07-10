package com.quickaudit.app.security

import com.quickaudit.app.model.User
import com.quickaudit.app.model.UserRole
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {
    
    @Value("\${jwt.secret:defaultSecretKeyForDevelopmentOnly}")
    private lateinit var secret: String
    
    @Value("\${jwt.expiration:86400000}") // 24 hours in milliseconds
    private var expiration: Long = 86400000
    
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }
    
    fun generateToken(user: User): String {
        val claims = HashMap<String, Any>()
        claims["userId"] = user.id
        claims["username"] = user.username
        claims["email"] = user.email ?: ""
        claims["role"] = user.role.name
        claims["fullName"] = user.fullName ?: ""
        
        return createToken(claims, user.username)
    }
    
    fun generateToken(userDetails: UserDetails): String {
        val claims = HashMap<String, Any>()
        claims["username"] = userDetails.username
        claims["authorities"] = userDetails.authorities.map { it.authority }
        
        return createToken(claims, userDetails.username)
    }
    
    private fun createToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }
    
    fun extractUsername(token: String): String? {
        return try {
            extractAllClaims(token).subject
        } catch (e: Exception) {
            null
        }
    }
    
    fun extractUserId(token: String): Long? {
        return try {
            val claims = extractAllClaims(token)
            claims["userId"]?.toString()?.toLong()
        } catch (e: Exception) {
            null
        }
    }
    
    fun extractUserRole(token: String): UserRole? {
        return try {
            val claims = extractAllClaims(token)
            val roleString = claims["role"]?.toString()
            roleString?.let { UserRole.valueOf(it) }
        } catch (e: Exception) {
            null
        }
    }
    
    fun extractExpiration(token: String): Date? {
        return try {
            extractAllClaims(token).expiration
        } catch (e: Exception) {
            null
        }
    }
    
    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
    
    fun isTokenExpired(token: String): Boolean {
        val expiration = extractExpiration(token)
        return expiration?.before(Date()) ?: true
    }
    
    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }
    
    fun validateToken(token: String): Boolean {
        return try {
            !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }
    
    fun extractUserInfo(token: String): UserInfo? {
        return try {
            val claims = extractAllClaims(token)
            UserInfo(
                userId = claims["userId"]?.toString()?.toLong() ?: 0L,
                username = claims["username"]?.toString() ?: "",
                email = claims["email"]?.toString() ?: "",
                role = claims["role"]?.toString()?.let { UserRole.valueOf(it) } ?: UserRole.USER,
                fullName = claims["fullName"]?.toString() ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }
}

data class UserInfo(
    val userId: Long,
    val username: String,
    val email: String,
    val role: UserRole,
    val fullName: String
) 