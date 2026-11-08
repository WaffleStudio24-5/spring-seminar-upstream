package com.wafflestudio.spring2026.auth.controller

import com.wafflestudio.spring2026.auth.dto.LoginRequest
import com.wafflestudio.spring2026.auth.dto.LoginResponse
import com.wafflestudio.spring2026.auth.dto.SignupRequest
import com.wafflestudio.spring2026.auth.dto.UserResponse
import com.wafflestudio.spring2026.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody request: SignupRequest,
    ): ResponseEntity<UserResponse> {
        val user = authService.signup(
            email = request.email,
            password = request.password,
            nickname = request.nickname,
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(UserResponse.from(user))
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<LoginResponse> {
        val accessToken = authService.login(
            email = request.email,
            password = request.password,
        )

        return ResponseEntity.ok(LoginResponse(accessToken))
    }
}
