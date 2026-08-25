package com.wafflestudio.spring2026.hello.controller

import com.wafflestudio.spring2026.hello.service.GreetingService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController(
    private val greetingService: GreetingService,
) {
    @GetMapping("/hello")
    fun hello(): Map<String, String> = mapOf("message" to greetingService.createGreeting())
}
