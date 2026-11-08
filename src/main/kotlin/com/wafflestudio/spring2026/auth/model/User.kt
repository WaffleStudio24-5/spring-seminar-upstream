package com.wafflestudio.spring2026.auth.model

class User(
    val id: Long,
    val email: String,
    val passwordHash: String,
    val nickname: String,
)
