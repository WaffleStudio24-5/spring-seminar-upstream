package com.wafflestudio.spring2026.auth.dto

import com.wafflestudio.spring2026.auth.model.User

data class UserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
) {
    companion object {
        fun from(user: User): UserResponse =
            UserResponse(
                id = user.id,
                email = user.email,
                nickname = user.nickname,
            )
    }
}
