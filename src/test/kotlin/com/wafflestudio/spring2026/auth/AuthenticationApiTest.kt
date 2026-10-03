package com.wafflestudio.spring2026.auth

import com.wafflestudio.spring2026.support.ApiIntegrationTest
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.get
import java.time.Instant
import java.util.Date
import kotlin.test.Test

class AuthenticationApiTest : ApiIntegrationTest() {
    @Test
    fun `가입한 사용자는 승인 전에도 로그인하고 내 정보를 조회할 수 있다`() {
        val pending = pendingRookie()
        getAs(pending.token, "/users/me").andExpect {
            status { isOk() }
            jsonPath("$.id") { value(pending.id) }
            jsonPath("$.role") { value("ROOKIE") }
            jsonPath("$.status") { value("PENDING") }
        }

        val rejected = rejectedRookie()
        getAs(rejected.token, "/users/me").andExpect {
            status { isOk() }
            jsonPath("$.id") { value(rejected.id) }
            jsonPath("$.status") { value("REJECTED") }
        }

        getAs(adminToken(), "/users/me").andExpect {
            status { isOk() }
            jsonPath("$.email") { value(ADMIN_EMAIL) }
            jsonPath("$.role") { value("ADMIN") }
        }
    }

    @Test
    fun `이메일이나 비밀번호가 틀리면 401, 비어 있으면 400을 반환한다`() {
        val email = uniqueEmail()
        signupRookie(email)

        loginRequest(email, PASSWORD).andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { isNotEmpty() }
        }

        loginRequest(email, "wrong-password").andExpect {
            status { isUnauthorized() }
        }

        loginRequest(uniqueEmail(), PASSWORD).andExpect {
            status { isUnauthorized() }
        }

        loginRequest(email, "   ").andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `토큰이 없거나 Bearer 형식이 아니면 401을 반환한다`() {
        val seminarId = createSeminar()
        val validToken = approvedRookie().token

        getAs(null, "/users/me").andExpect { status { isUnauthorized() } }
        getAs(null, "/users/me/enrollments").andExpect { status { isUnauthorized() } }
        getAs(null, "/seminars/$seminarId").andExpect { status { isUnauthorized() } }
        postAs(null, "/seminars/$seminarId/enrollments").andExpect { status { isUnauthorized() } }

        // Bearer 로 시작하지 않음
        mockMvc.get("/users/me") {
            header(HttpHeaders.AUTHORIZATION, "Token $validToken")
        }.andExpect {
            status { isUnauthorized() }
        }

        // JWT 형식이 아님
        getAs("not-a-jwt", "/users/me").andExpect { status { isUnauthorized() } }

        // 인증 실패는 요청값 검증과 리소스 조회보다 먼저 판단한다.
        postAs(null, "/seminars", """{"title":""}""").andExpect { status { isUnauthorized() } }
        getAs(null, "/seminars/999999").andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `서명이 올바르지 않은 토큰은 401을 반환한다`() {
        val rookie = approvedRookie()

        // 발급받은 토큰의 서명을 바꾼다.
        val (header, payload, signature) = rookie.token.split(".")
        val replaced = if (signature.first() == 'A') 'B' else 'A'
        val tampered = "$header.$payload.$replaced${signature.drop(1)}"
        getAs(tampered, "/users/me").andExpect { status { isUnauthorized() } }

        // 서버가 모르는 키로 서명한 토큰
        val foreignKey = Keys.hmacShaKeyFor("this-is-not-the-server-secret-key-0123456789".toByteArray())
        val forged = Jwts.builder()
            .subject(rookie.id.toString())
            .claim("userId", rookie.id)
            .claim("role", "ADMIN")
            .issuedAt(Date())
            .expiration(Date.from(Instant.now().plusSeconds(3600)))
            .signWith(foreignKey)
            .compact()
        getAs(forged, "/users/me").andExpect { status { isUnauthorized() } }
        postAs(forged, "/seminars", mapOf("title" to "Forged")).andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `세미나 목록은 로그인 없이 조회하고 제목으로 검색할 수 있다`() {
        val keyword = unique("Keyword")
        val firstId = createSeminar(title = "$keyword first")
        val secondId = createSeminar(title = "$keyword second")

        getAs(null, "/seminars?keyword=$keyword").andExpect {
            status { isOk() }
            jsonPath("$.totalElements") { value(2) }
            jsonPath("$.totalPages") { value(1) }
            jsonPath("$.page") { value(0) }
            jsonPath("$.content.length()") { value(2) }
            jsonPath("$.content[0].id") { value(firstId) }
            jsonPath("$.content[1].id") { value(secondId) }
            jsonPath("$.content[0].status") { value("OPEN") }
            jsonPath("$.content[0].enrolledCount") { value(0) }
        }
    }
}
