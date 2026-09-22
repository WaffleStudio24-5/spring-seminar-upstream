package com.wafflestudio.spring2026.auth.service

import com.wafflestudio.spring2026.auth.EmailAlreadyExistsException
import com.wafflestudio.spring2026.auth.InvalidCredentialsException
import com.wafflestudio.spring2026.auth.model.User
import com.wafflestudio.spring2026.auth.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    fun signup(
        email: String,
        password: String,
        nickname: String,
    ): User {
        if (userRepository.findByEmail(email) != null) {
            throw EmailAlreadyExistsException(email)
        }

        return userRepository.save(
            email = email,
            passwordHash = passwordHasher.hash(password),
            nickname = nickname,
        )
    }

    fun login(
        email: String,
        password: String,
    ): String {
        val user = userRepository.findByEmail(email)

        if (user == null || !passwordHasher.matches(password, user.passwordHash)) {
            throw InvalidCredentialsException()
        }

        return jwtTokenProvider.issue(user.id)
    }
}
