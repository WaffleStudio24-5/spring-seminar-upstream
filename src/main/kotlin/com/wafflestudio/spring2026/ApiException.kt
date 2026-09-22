package com.wafflestudio.spring2026

import org.springframework.http.HttpStatus

/**
 * API 로 그대로 내보낼 수 있는 예외의 공통 부모.
 *
 * 예외마다 상태 코드와 오류 코드를 자기가 들고 있으면,
 * 기능이 늘어도 예외를 응답으로 바꾸는 곳은 [GlobalExceptionHandler] 한 군데로 유지된다.
 * 구체적인 예외는 각 엔티티 패키지 안에 둔다.
 */
abstract class ApiException(
    val status: HttpStatus,
    val code: String,
    override val message: String,
) : RuntimeException(message)
