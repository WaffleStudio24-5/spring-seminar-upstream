package com.wafflestudio.spring2026.user

import com.wafflestudio.spring2026.support.ApiIntegrationTest
import kotlin.test.Test
import kotlin.test.assertTrue

class UserApprovalApiTest : ApiIntegrationTest() {
    @Test
    fun `루키가 가입 신청하면 대기 상태의 사용자 정보를 조회할 수 있다`() {
        val email = uniqueEmail()
        val rookieId = signupRookie(email)

        getAs(adminToken(), "/users/$rookieId").andExpect {
            status { isOk() }
            jsonPath("$.id") { value(rookieId) }
            jsonPath("$.email") { value(email) }
            jsonPath("$.role") { value("ROOKIE") }
            jsonPath("$.status") { value("PENDING") }
            jsonPath("$.createdAt") { exists() }
        }
    }

    @Test
    fun `운영진은 담당 세미나와 함께 가입 신청한다`() {
        val seminarId = createSeminar()
        val staffId = signupStaff(seminarId)

        getAs(adminToken(), "/users/$staffId").andExpect {
            status { isOk() }
            jsonPath("$.role") { value("STAFF") }
            jsonPath("$.seminarId") { value(seminarId) }
            jsonPath("$.status") { value("PENDING") }
        }
    }

    @Test
    fun `대기 중인 가입 신청은 승인 또는 반려할 수 있다`() {
        val approvedUserId = signupRookie()
        val rejectedUserId = signupRookie()

        approve(approvedUserId).andExpect {
            status { isOk() }
            jsonPath("$.id") { value(approvedUserId) }
            jsonPath("$.status") { value("APPROVED") }
        }

        approve(rejectedUserId, "REJECTED").andExpect {
            status { isOk() }
            jsonPath("$.id") { value(rejectedUserId) }
            jsonPath("$.status") { value("REJECTED") }
        }
    }

    @Test
    fun `유효하지 않은 가입 또는 심사 요청은 400을 반환한다`() {
        postAs(
            null,
            "/auth/signup",
            """{"email":"invalid","password":"","name":"","githubUsername":"","role":"ADMIN"}""",
        ).andExpect {
            status { isBadRequest() }
        }

        val pendingUserId = signupRookie()
        approve(pendingUserId, "PENDING").andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `가입과 심사의 대표 오류 상태를 반환한다`() {
        postAs(
            null,
            "/auth/signup",
            """{"email":"${uniqueEmail()}","password":"password","name":"Staff","githubUsername":"staff","role":"STAFF","seminarId":999999}""",
        ).andExpect {
            status { isNotFound() }
        }

        val email = uniqueEmail()
        signupRookie(email)
        postAs(
            null,
            "/auth/signup",
            """{"email":"$email","password":"password","name":"Duplicate","githubUsername":"duplicate","role":"ROOKIE"}""",
        ).andExpect {
            status { isConflict() }
        }

        val admin = adminToken()
        getAs(admin, "/users/999999").andExpect {
            status { isNotFound() }
        }

        approve(999999, token = admin).andExpect {
            status { isNotFound() }
        }

        val approvedUserId = signupRookie()
        approve(approvedUserId, token = admin).andExpect { status { isOk() } }
        approve(approvedUserId, "REJECTED", admin).andExpect {
            status { isConflict() }
        }
    }

    @Test
    fun `가입 신청 목록의 status 나 role 이 정해진 값이 아니면 400을 반환한다`() {
        val admin = adminToken()

        getAs(admin, "/users?status=UNKNOWN").andExpect { status { isBadRequest() } }
        getAs(admin, "/users?role=ADMIN").andExpect { status { isBadRequest() } }
    }

    @Test
    fun `가입 신청 목록은 상태로 거르고 최근 가입 순으로 보여 주며 와장은 나오지 않는다`() {
        val admin = adminToken()
        val olderId = signupRookie()
        val newerId = signupRookie()
        val approvedId = signupRookie()
        approve(approvedId, token = admin).andExpect { status { isOk() } }

        // 최근에 가입한 순서. 방금 가입한 두 대기 루키가 맨 앞에 온다.
        val pendingBody = getAs(admin, "/users?status=PENDING&role=ROOKIE&size=100").andExpect {
            status { isOk() }
            jsonPath("$.content[0].id") { value(newerId) }
            jsonPath("$.content[1].id") { value(olderId) }
        }.andReturn().response.contentAsString
        val pending = objectMapper.readTree(pendingBody).path("content").toList()
        assertTrue(pending.all { it.path("status").asString() == "PENDING" }, "status=PENDING 이면 대기 중인 신청만 보여야 합니다.")
        assertTrue(pending.none { it.path("id").asLong() == approvedId })

        // 와장은 승인된 사용자이지만 가입 신청이 아니므로 어느 페이지에도 나오지 않는다.
        var page = 0
        val approvedIds = mutableListOf<Long>()
        while (true) {
            val body = getAs(admin, "/users?status=APPROVED&size=100&page=$page").andExpect {
                status { isOk() }
            }.andReturn().response.contentAsString
            val content = objectMapper.readTree(body).path("content").toList()
            if (content.isEmpty()) break
            assertTrue(content.none { it.path("email").asString() == ADMIN_EMAIL }, "와장 계정은 가입 신청 목록에 나오지 않아야 합니다.")
            assertTrue(content.none { it.path("role").asString() == "ADMIN" })
            approvedIds += content.map { it.path("id").asLong() }
            page++
        }
        assertTrue(approvedId in approvedIds)
    }
}
