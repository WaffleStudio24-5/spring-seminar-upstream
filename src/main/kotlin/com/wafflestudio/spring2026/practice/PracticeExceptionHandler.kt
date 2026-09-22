package com.wafflestudio.spring2026.practice

import com.wafflestudio.spring2026.ApiErrorResponse
import com.wafflestudio.spring2026.practice.model.PracticeException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 이 예제가 던지는 예외를 1주차에 만든 [ApiErrorResponse] 형태로 바꿔 준다.
 *
 * 요청 값 검증 실패(`MethodArgumentNotValidException`)는 이미 `GlobalExceptionHandler`
 * 가 처리하므로 여기서 다시 다루지 않는다.
 */
@RestControllerAdvice(basePackageClasses = [PracticeExceptionHandler::class])
class PracticeExceptionHandler {
    @ExceptionHandler(PracticeException::class)
    fun handlePracticeException(exception: PracticeException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(exception.status).body(
            ApiErrorResponse(code = exception.code, message = exception.message),
        )
}
