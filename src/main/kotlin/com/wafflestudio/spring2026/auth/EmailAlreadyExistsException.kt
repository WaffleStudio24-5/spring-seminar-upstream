package com.wafflestudio.spring2026.auth

class EmailAlreadyExistsException(
    val email: String,
) : RuntimeException(
        "이미 가입된 이메일입니다: $email",
    )
