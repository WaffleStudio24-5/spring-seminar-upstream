package com.wafflestudio.spring2026.hello.service

import org.springframework.stereotype.Service

@Service
class GreetingService {
    fun createGreeting(): String = "Hello, Spring Boot!"
}
