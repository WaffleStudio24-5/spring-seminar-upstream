package com.wafflestudio.spring2026

data class ApiErrorResponse(
    val code: String,
    val message: String,
    val fieldErrors: List<FieldErrorResponse> = emptyList(),
)

data class FieldErrorResponse(
    val field: String,
    val message: String,
)
