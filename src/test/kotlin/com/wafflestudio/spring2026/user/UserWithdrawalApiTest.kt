package com.wafflestudio.spring2026.user

import com.wafflestudio.spring2026.support.Week4ApiIntegrationTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserWithdrawalApiTest : Week4ApiIntegrationTest() {
    @Test
    fun `탈퇴하면 다시 로그인할 수 없고 같은 이메일로 다시 가입할 수 없다`() {
        val email = uniqueEmail()
        val token = approvedRookieToken(email)

        deleteAs(token, "/users/me").andExpect { status { isNoContent() } }

        loginRequest(email, PASSWORD).andExpect { status { isUnauthorized() } }
        postAs(
            null,
            "/auth/signup",
            mapOf(
                "email" to email,
                "password" to PASSWORD,
                "name" to "Waffle",
                "githubUsername" to "waffle",
                "role" to "ROOKIE",
            ),
        ).andExpect { status { isConflict() } }
    }

    @Test
    fun `탈퇴하면 신청 기간 중인 세미나의 수강 신청이 취소되어 자리가 빈다`() {
        val seminarId = createSeminar(capacity = 1)
        val sessionId = createSession(seminarId)
        val leaving = enrolledRookie(seminarId)
        val waiting = approvedRookie()
        updateRecords(
            sessionId,
            adminToken(),
            recordPatch(leaving.enrollmentId, "attendance" to "PRESENT", "assignmentStatus" to "PASSED"),
        ).andExpect { status { isNoContent() } }
        enroll(seminarId, waiting.token).andExpect { status { isConflict() } }

        deleteAs(leaving.actor.token, "/users/me").andExpect { status { isNoContent() } }

        val waitingEnrollmentId = enrollSuccessfully(seminarId, waiting)
        val rows = dashboard(seminarId, adminToken()).path("content").toList()
        assertEquals(listOf(waitingEnrollmentId), rows.map { it.path("id").asLong() })
        getAs(adminToken(), "/seminars/$seminarId").andExpect {
            status { isOk() }
            jsonPath("$.enrolledCount") { value(1) }
        }
    }

    @Test
    fun `와장은 탈퇴할 수 없다`() {
        deleteAs(adminToken(), "/users/me").andExpect { status { isConflict() } }

        loginRequest(ADMIN_EMAIL, ADMIN_PASSWORD).andExpect { status { isOk() } }
    }

    private fun approvedRookieToken(email: String): String {
        approve(signupRookie(email)).andExpect { status { isOk() } }

        return login(email)
    }
}
