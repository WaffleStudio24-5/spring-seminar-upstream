package com.wafflestudio.spring2026.auth.service

import java.security.SecureRandom
import java.time.Instant
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.springframework.stereotype.Component

// 별도 의존성 없이 HS256 JWT 액세스 토큰을 발급한다.
@Component
class JwtTokenProvider {
    // 서명 키는 서버가 뜰 때 새로 만든다. 사용자 저장소가 인메모리라 재시작 시 토큰이 무효가 되어도 문제가 없고,
    // 저장소에 비밀 값을 커밋하지 않아도 된다.
    private val secret = ByteArray(32).also(SecureRandom()::nextBytes)

    fun issue(userId: Long): String {
        val now = Instant.now().epochSecond
        val header = """{"alg":"HS256","typ":"JWT"}"""
        val payload = """{"sub":"$userId","iat":$now,"exp":${now + EXPIRES_IN_SECONDS}}"""

        val content = "${encode(header.toByteArray())}.${encode(payload.toByteArray())}"

        return "$content.${encode(sign(content))}"
    }

    private fun sign(content: String): ByteArray =
        Mac.getInstance("HmacSHA256")
            .apply { init(SecretKeySpec(secret, "HmacSHA256")) }
            .doFinal(content.toByteArray())

    private fun encode(bytes: ByteArray): String = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)

    private companion object {
        const val EXPIRES_IN_SECONDS = 60 * 60L
    }
}
