package com.wafflestudio.spring2026

import com.wafflestudio.spring2026.auth.EmailAlreadyExistsException
import com.wafflestudio.spring2026.auth.InvalidCredentialsException
import com.wafflestudio.spring2026.meeting.MeetingNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
        exception: MethodArgumentNotValidException,
    ): ResponseEntity<ApiErrorResponse> {
        val fieldErrors = exception.bindingResult.fieldErrors.map { error ->
            FieldErrorResponse(
                field = error.field,
                message = error.defaultMessage ?: "잘못된 값입니다.",
            )
        }

        return ResponseEntity.badRequest().body(
            ApiErrorResponse(
                code = "INVALID_REQUEST",
                message = "요청값이 올바르지 않습니다.",
                fieldErrors = fieldErrors,
            ),
        )
    }

    @ExceptionHandler(MeetingNotFoundException::class)
    fun handleMeetingNotFound(
        exception: MeetingNotFoundException,
    ): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiErrorResponse(
                code = "MEETING_NOT_FOUND",
                message = exception.message ?: "모임을 찾을 수 없습니다.",
            ),
        )

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExists(
        exception: EmailAlreadyExistsException,
    ): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(
            ApiErrorResponse(
                code = "EMAIL_ALREADY_EXISTS",
                message = exception.message ?: "이미 가입된 이메일입니다.",
            ),
        )

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(
        exception: InvalidCredentialsException,
    ): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiErrorResponse(
                code = "INVALID_CREDENTIALS",
                message = exception.message ?: "이메일 또는 비밀번호가 올바르지 않습니다.",
            ),
        )
}
