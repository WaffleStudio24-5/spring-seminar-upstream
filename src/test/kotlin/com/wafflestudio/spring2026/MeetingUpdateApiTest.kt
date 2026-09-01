package com.wafflestudio.spring2026

import org.hamcrest.Matchers.containsInAnyOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MeetingUpdateApiTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `updates only the supplied meeting fields`() {
        val location = createMeeting("Spring study", 10)
        val id = location.substringAfterLast('/').toInt()

        mockMvc.patch(location) {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title":"Spring Boot study"}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(id) }
            jsonPath("$.title") { value("Spring Boot study") }
            jsonPath("$.capacity") { value(10) }
        }

        mockMvc.patch(location) {
            contentType = MediaType.APPLICATION_JSON
            content = """{"capacity":20}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(id) }
            jsonPath("$.title") { value("Spring Boot study") }
            jsonPath("$.capacity") { value(20) }
        }

        mockMvc.patch(location) {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title":"Kotlin study","capacity":15}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(id) }
            jsonPath("$.title") { value("Kotlin study") }
            jsonPath("$.capacity") { value(15) }
        }
    }

    @Test
    fun `rejects invalid update fields`() {
        val location = createMeeting("Spring study", 10)

        mockMvc.patch(location) {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title":"   ","capacity":0}"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("INVALID_REQUEST") }
            jsonPath("$.fieldErrors[*].field") {
                value(containsInAnyOrder("title", "capacity"))
            }
        }
    }

    @Test
    fun `returns not found when updating a missing meeting`() {
        mockMvc.patch("/meetings/999") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"capacity":20}"""
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.code") { value("MEETING_NOT_FOUND") }
            jsonPath("$.message") { value("ID가 999인 모임을 찾을 수 없습니다.") }
            jsonPath("$.fieldErrors") { isEmpty() }
        }
    }

    private fun createMeeting(title: String, capacity: Int): String =
        mockMvc.post("/meetings") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title":"$title","capacity":$capacity}"""
        }.andExpect {
            status { isCreated() }
        }.andReturn().response.getHeader("Location")!!
}
