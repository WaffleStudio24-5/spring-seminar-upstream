package com.wafflestudio.spring2026.auth.repository

import com.wafflestudio.spring2026.auth.model.User
import org.springframework.stereotype.Repository

@Repository
class UserRepository {
    // 세미나용 인메모리 저장소. 동시 요청을 고려하지 않으며 서버를 재시작하면 비워진다.
    private val users = mutableMapOf<Long, User>()
    private var nextId = 1L

    fun save(
        email: String,
        passwordHash: String,
        nickname: String,
    ): User {
        val user = User(
            id = nextId,
            email = email,
            passwordHash = passwordHash,
            nickname = nickname,
        )

        nextId += 1
        users[user.id] = user

        return user
    }

    fun findByEmail(email: String): User? = users.values.find { it.email == email }
}
