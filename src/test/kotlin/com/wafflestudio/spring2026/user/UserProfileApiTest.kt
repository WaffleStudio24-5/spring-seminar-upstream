package com.wafflestudio.spring2026.user

import com.wafflestudio.spring2026.support.ApiIntegrationTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserProfileApiTest : ApiIntegrationTest() {
    @Test
    fun `보낸 항목만 수정하고 수정된 내 정보를 응답한다`() {
        val rookie = approvedRookie()
        val before = me(rookie.token)

        patchAs(rookie.token, "/users/me", mapOf("name" to "새이름")).andExpect {
            status { isOk() }
            jsonPath("$.id") { value(rookie.id) }
            jsonPath("$.name") { value("새이름") }
            jsonPath("$.githubUsername") { value(before.path("githubUsername").asString()) }
        }
        patchAs(rookie.token, "/users/me", mapOf("githubUsername" to "new-github")).andExpect {
            status { isOk() }
            jsonPath("$.name") { value("새이름") }
            jsonPath("$.githubUsername") { value("new-github") }
        }

        val after = me(rookie.token)
        assertEquals("새이름", after.path("name").asString())
        assertEquals("new-github", after.path("githubUsername").asString())
    }

    @Test
    fun `이메일과 역할과 가입 상태는 바꿀 수 없다`() {
        val rookie = approvedRookie()
        val before = me(rookie.token)

        patchAs(
            rookie.token,
            "/users/me",
            mapOf("name" to "새이름", "email" to "changed@example.com", "role" to "ADMIN", "status" to "PENDING"),
        ).andExpect {
            status { isOk() }
        }

        val after = me(rookie.token)
        assertEquals("새이름", after.path("name").asString())
        assertEquals(before.path("email").asString(), after.path("email").asString())
        assertEquals("ROOKIE", after.path("role").asString())
        assertEquals("APPROVED", after.path("status").asString())
    }

    @Test
    fun `이름이나 GitHub 사용자명이 공백이면 400이고 수정하지 않는다`() {
        val rookie = approvedRookie()
        val before = me(rookie.token)

        patchAs(rookie.token, "/users/me", mapOf("name" to "   ")).andExpect { status { isBadRequest() } }
        patchAs(rookie.token, "/users/me", mapOf("githubUsername" to "")).andExpect { status { isBadRequest() } }
        patchAs(rookie.token, "/users/me", mapOf("name" to "새이름", "githubUsername" to " ")).andExpect {
            status { isBadRequest() }
        }

        val after = me(rookie.token)
        assertEquals(before.path("name").asString(), after.path("name").asString())
        assertEquals(before.path("githubUsername").asString(), after.path("githubUsername").asString())
    }

    private fun me(token: String) = responseJson(getAs(token, "/users/me").andExpect { status { isOk() } })
}
