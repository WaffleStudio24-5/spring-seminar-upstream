package com.wafflestudio.spring2026

import org.springframework.http.HttpStatus

abstract class ApiException(
    val status: HttpStatus,
    val code: String,
    override val message: String,
) : RuntimeException(message)
