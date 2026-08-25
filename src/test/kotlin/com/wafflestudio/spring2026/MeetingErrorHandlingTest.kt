package com.wafflestudio.spring2026

import org.hamcrest.Matchers.containsInAnyOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class MeetingErrorHandlingTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `validation and missing meeting return error responses`() {
        mockMvc.post("/meetings") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title":"","capacity":0}"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("INVALID_REQUEST") }
            jsonPath("$.fieldErrors[*].field") {
                value(containsInAnyOrder("title", "capacity"))
            }
        }

        mockMvc.get("/meetings/999").andExpect {
            status { isNotFound() }
            jsonPath("$.code") { value("MEETING_NOT_FOUND") }
            jsonPath("$.fieldErrors") { isEmpty() }
        }
    }
}
