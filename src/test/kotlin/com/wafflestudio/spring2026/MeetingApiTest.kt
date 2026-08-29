package com.wafflestudio.spring2026

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
class MeetingApiTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `creates and retrieves a meeting`() {
        val location = mockMvc.post("/meetings") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title":"Spring study","capacity":10}"""
        }.andExpect {
            status { isCreated() }
            header { exists("Location") }
            jsonPath("$.title") { value("Spring study") }
            jsonPath("$.capacity") { value(10) }
        }.andReturn().response.getHeader("Location")!!

        mockMvc.get(location).andExpect {
            status { isOk() }
            jsonPath("$.title") { value("Spring study") }
            jsonPath("$.capacity") { value(10) }
        }
    }
}
