package com.quickaudit.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class QuickAuditAppBackendApplication

fun main(args: Array<String>) {
    runApplication<QuickAuditAppBackendApplication>(*args)
} 