package com.wafflestudio.spring2026.auth

// 이메일이 없는 경우와 비밀번호가 틀린 경우를 구분하지 않는다. 가입된 이메일을 추측할 단서를 주지 않기 위함이다.
class InvalidCredentialsException : RuntimeException(
    "이메일 또는 비밀번호가 올바르지 않습니다.",
)
