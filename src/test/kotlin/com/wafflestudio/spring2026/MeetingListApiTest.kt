package com.wafflestudio.spring2026

import org.hamcrest.Matchers.containsInAnyOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MeetingListApiTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `returns an empty array when no meetings exist`() {
        mockMvc.get("/meetings").andExpect {
            status { isOk() }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
            jsonPath("$") { isArray() }
            jsonPath("$") { isEmpty() }
        }
    }

    @Test
    fun `returns every created meeting`() {
        createMeeting("Spring study", 10)
        createMeeting("Kotlin study", 15)

        mockMvc.get("/meetings").andExpect {
            status { isOk() }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
            jsonPath("$.length()") { value(2) }
            jsonPath("$[*].title") {
                value(containsInAnyOrder("Spring study", "Kotlin study"))
            }
            jsonPath("$[*].capacity") {
                value(containsInAnyOrder(10, 15))
            }
        }
    }

    private fun createMeeting(title: String, capacity: Int) {
        mockMvc.post("/meetings") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title":"$title","capacity":$capacity}"""
        }.andExpect {
            status { isCreated() }
        }
    }
}
