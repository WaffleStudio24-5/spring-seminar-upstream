package com.wafflestudio.spring2026.enrollment

import com.wafflestudio.spring2026.support.ApiIntegrationTest
import kotlin.test.Test

class EnrollmentLifecycleApiTest : ApiIntegrationTest() {
    @Test
    fun `승인된 루키는 세미나의 그레이스 데이로 수강 신청한다`() {
        val seminarId = createSeminar(totalGraceDays = 4)
        val rookie = approvedRookie()

        enroll(seminarId, rookie.token).andExpect {
            status { isCreated() }
            jsonPath("$.graceDaysRemaining") { value(4) }
            jsonPath("$.createdAt") { exists() }
        }

        getAs(rookie.token, "/seminars/$seminarId").andExpect {
            status { isOk() }
            jsonPath("$.enrolledCount") { value(1) }
        }
    }

    @Test
    fun `중복 또는 신청 불가 상태의 수강 신청은 409를 반환한다`() {
        val openSeminarId = createSeminar()
        val rookie = approvedRookie()
        enroll(openSeminarId, rookie.token).andExpect { status { isCreated() } }

        enroll(openSeminarId, rookie.token).andExpect {
            status { isConflict() }
        }

        val futureSeminarId = createSeminar(
            applyStartAt = now().plusDays(1),
            applyEndAt = now().plusDays(2),
        )
        enroll(futureSeminarId, approvedRookie().token).andExpect {
            status { isConflict() }
        }
    }

    @Test
    fun `정원이 찬 세미나를 취소하면 자리가 열린다`() {
        val seminarId = createSeminar(capacity = 1)
        val first = approvedRookie()
        enroll(seminarId, first.token).andExpect { status { isCreated() } }

        getAs(first.token, "/seminars/$seminarId").andExpect {
            status { isOk() }
            jsonPath("$.enrolledCount") { value(1) }
            jsonPath("$.status") { value("CLOSED") }
        }

        cancelEnrollment(seminarId, first.token).andExpect {
            status { isNoContent() }
            content { string("") }
        }

        getAs(first.token, "/seminars/$seminarId").andExpect {
            status { isOk() }
            jsonPath("$.enrolledCount") { value(0) }
            jsonPath("$.status") { value("OPEN") }
        }

        enroll(seminarId, approvedRookie().token).andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `없는 수강 신청 리소스는 404를 반환한다`() {
        val seminarId = createSeminar()
        val rookie = approvedRookie()

        enroll(999999, rookie.token).andExpect {
            status { isNotFound() }
        }

        // 신청하지 않은 세미나의 신청을 취소할 수는 없다.
        cancelEnrollment(seminarId, rookie.token).andExpect {
            status { isNotFound() }
        }
    }
}
