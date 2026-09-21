package com.wafflestudio.spring2026

import com.wafflestudio.spring2026.support.ApiIntegrationTest
import org.hamcrest.Matchers.containsInAnyOrder
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import kotlin.test.Test

class MeetingErrorHandlingTest : ApiIntegrationTest() {
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
