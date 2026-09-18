package com.wafflestudio.spring2026.auth.service

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import org.springframework.stereotype.Component

// 별도 의존성 없이 JDK의 PBKDF2로 비밀번호를 해시한다. 저장 형식은 "반복횟수:salt:hash" 이다.
@Component
class PasswordHasher {
    private val random = SecureRandom()

    fun hash(password: String): String {
        val salt = ByteArray(SALT_BYTES).also(random::nextBytes)
        val hash = pbkdf2(password, salt, ITERATIONS)

        return "$ITERATIONS:${encoder.encodeToString(salt)}:${encoder.encodeToString(hash)}"
    }

    fun matches(
        password: String,
        stored: String,
    ): Boolean {
        val (iterations, salt, expected) = stored.split(":")
        val actual = pbkdf2(password, decoder.decode(salt), iterations.toInt())

        // 일치 여부에 따라 비교 시간이 달라지지 않도록 상수 시간 비교를 쓴다.
        return MessageDigest.isEqual(actual, decoder.decode(expected))
    }

    private fun pbkdf2(
        password: String,
        salt: ByteArray,
        iterations: Int,
    ): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, HASH_BITS)

        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
    }

    private companion object {
        const val ITERATIONS = 310_000
        const val SALT_BYTES = 16
        const val HASH_BITS = 256
        val encoder: Base64.Encoder = Base64.getEncoder()
        val decoder: Base64.Decoder = Base64.getDecoder()
    }
}
