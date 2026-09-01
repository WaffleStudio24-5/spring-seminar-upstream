package com.wafflestudio.spring2026

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MeetingDeleteApiTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `deletes a meeting without a response body`() {
        val deletedLocation = createMeeting("Spring study", 10)
        createMeeting("Kotlin study", 15)

        mockMvc.delete(deletedLocation).andExpect {
            status { isNoContent() }
            content { string("") }
        }

        mockMvc.get(deletedLocation).andExpect {
            status { isNotFound() }
            jsonPath("$.code") { value("MEETING_NOT_FOUND") }
        }

        mockMvc.get("/meetings").andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].title") { value("Kotlin study") }
            jsonPath("$[0].capacity") { value(15) }
        }
    }

    @Test
    fun `returns not found when deleting a missing meeting`() {
        mockMvc.delete("/meetings/999").andExpect {
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
