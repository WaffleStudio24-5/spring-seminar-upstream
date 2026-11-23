package com.wafflestudio.spring2026.session

import com.wafflestudio.spring2026.support.Week4ApiIntegrationTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SessionDeletionApiTest : Week4ApiIntegrationTest() {
    @Test
    fun `마지막 회차는 등록한 순서가 아니라 시작 시각으로 정한다`() {
        val seminarId = createSeminar()
        val later = createSession(seminarId, startsAt = now().plusDays(2))
        val earlier = createSession(seminarId, startsAt = now().plusDays(1))
        val admin = adminToken()

        deleteAs(admin, "/sessions/$earlier").andExpect { status { isConflict() } }
        assertEquals(listOf(earlier, later), sessionIds(seminarId))

        deleteAs(admin, "/sessions/$later").andExpect { status { isNoContent() } }
        deleteAs(admin, "/sessions/$earlier").andExpect { status { isNoContent() } }
        assertEquals(emptyList(), sessionIds(seminarId))
    }

    @Test
    fun `출석이나 과제 기록이 있는 회차는 삭제할 수 없다`() {
        val seminarId = createSeminar()
        val withAttendance = createSession(seminarId, startsAt = now().plusDays(1))
        val rookie = enrolledRookie(seminarId)
        val admin = adminToken()
        updateRecords(withAttendance, admin, recordPatch(rookie.enrollmentId, "attendance" to "ABSENT"))
            .andExpect { status { isNoContent() } }

        deleteAs(admin, "/sessions/$withAttendance").andExpect { status { isConflict() } }

        val withAssignment = createSession(seminarId, startsAt = now().plusDays(2))
        updateRecords(withAssignment, admin, recordPatch(rookie.enrollmentId, "assignmentStatus" to "NOT_SUBMITTED"))
            .andExpect { status { isNoContent() } }

        deleteAs(admin, "/sessions/$withAssignment").andExpect { status { isConflict() } }
        assertEquals(listOf(withAttendance, withAssignment), sessionIds(seminarId))
    }

    @Test
    fun `와장과 담당 운영진만 삭제할 수 있다`() {
        val seminarId = createSeminar()
        val sessionId = createSession(seminarId)
        val otherStaff = approvedStaff(createSeminar())
        val rookie = enrolledRookie(seminarId)
        val staff = approvedStaff(seminarId)

        deleteAs(null, "/sessions/$sessionId").andExpect { status { isUnauthorized() } }
        deleteAs(otherStaff.token, "/sessions/$sessionId").andExpect { status { isForbidden() } }
        deleteAs(rookie.actor.token, "/sessions/$sessionId").andExpect { status { isForbidden() } }
        assertEquals(listOf(sessionId), sessionIds(seminarId))

        deleteAs(staff.token, "/sessions/$sessionId").andExpect { status { isNoContent() } }
    }

    private fun sessionIds(seminarId: Long): List<Long> =
        responseJson(getAs(adminToken(), "/seminars/$seminarId/sessions").andExpect { status { isOk() } })
            .toList()
            .map { it.path("id").asLong() }
}
