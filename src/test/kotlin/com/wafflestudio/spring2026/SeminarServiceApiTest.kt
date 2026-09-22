package com.wafflestudio.spring2026

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import tools.jackson.databind.ObjectMapper
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong
import kotlin.test.Test

/**
 * 수업 요구사항이 실제로 지켜지는지 확인한다.
 *
 * DB 는 Testcontainers 가 띄운 일회용 MySQL 이다. (`src/test/resources/application.yaml`)
 * 테스트끼리 데이터를 공유하므로, 각 테스트는 자기가 쓸 세미나와 학생을 직접 만든다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SeminarServiceApiTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
) {
    @Test
    fun `세미나에 학생과 모임을 만들고 참여할 수 있다`() {
        val seminarId = createSeminar("Spring 세미나")
        val studentId = createStudent(seminarId)
        val meetingId = createMeeting(seminarId, capacity = 10)

        mockMvc.get("/seminars/$seminarId/students").andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].id") { value(studentId) }
        }

        join(meetingId, studentId).andExpect {
            status { isCreated() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].id") { value(studentId) }
        }

        // 참여 인원은 저장하지 않고 participations 를 세어서 구한다.
        mockMvc.get("/meetings/$meetingId").andExpect {
            status { isOk() }
            jsonPath("$.participantCount") { value(1) }
        }

        mockMvc.delete("/meetings/$meetingId/participations/$studentId").andExpect {
            status { isNoContent() }
        }
        mockMvc.get("/meetings/$meetingId").andExpect {
            jsonPath("$.participantCount") { value(0) }
        }
    }

    @Test
    fun `학생은 자신이 속하지 않은 세미나의 모임에는 참여할 수 없다`() {
        val mySeminarId = createSeminar("Frontend 세미나")
        val otherSeminarId = createSeminar("FastAPI 세미나")
        val studentId = createStudent(mySeminarId)
        val otherMeetingId = createMeeting(otherSeminarId, capacity = 10)

        join(otherMeetingId, studentId).andExpect {
            status { isConflict() }
            jsonPath("$.code") { value("NOT_IN_SAME_SEMINAR") }
        }
    }

    @Test
    fun `같은 모임에 두 번 참여할 수 없고 정원을 넘길 수 없다`() {
        val seminarId = createSeminar("Android 세미나")
        val first = createStudent(seminarId)
        val second = createStudent(seminarId)
        val meetingId = createMeeting(seminarId, capacity = 1)

        join(meetingId, first).andExpect { status { isCreated() } }
        join(meetingId, first).andExpect {
            status { isConflict() }
            jsonPath("$.code") { value("ALREADY_PARTICIPATING") }
        }
        join(meetingId, second).andExpect {
            status { isConflict() }
            jsonPath("$.code") { value("MEETING_FULL") }
        }
    }

    @Test
    fun `없는 세미나나 학생을 가리키면 404를 반환한다`() {
        mockMvc.get("/seminars/999999").andExpect {
            status { isNotFound() }
            jsonPath("$.code") { value("SEMINAR_NOT_FOUND") }
        }
        mockMvc.get("/students/999999").andExpect { status { isNotFound() } }
        mockMvc.get("/meetings/999999").andExpect { status { isNotFound() } }

        mockMvc.post("/students") {
            json(mapOf("name" to "김와플", "age" to 20, "email" to uniqueEmail(), "seminarId" to 999999))
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.code") { value("SEMINAR_NOT_FOUND") }
        }
    }

    @Test
    fun `이메일은 중복될 수 없고 잘못된 요청은 400을 반환한다`() {
        val seminarId = createSeminar("iOS 세미나")
        val email = uniqueEmail()
        createStudent(seminarId, email)

        mockMvc.post("/students") {
            json(mapOf("name" to "최코틀", "age" to 21, "email" to email, "seminarId" to seminarId))
        }.andExpect {
            status { isConflict() }
            jsonPath("$.code") { value("EMAIL_ALREADY_EXISTS") }
        }

        mockMvc.post("/students") {
            json(mapOf("name" to "", "age" to 0, "email" to "not-an-email", "seminarId" to seminarId))
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("INVALID_REQUEST") }
        }
    }

    /* 준비용 헬퍼 ------------------------------------------------------ */

    private fun createSeminar(title: String): Long =
        idOf(
            mockMvc.post("/seminars") {
                json(mapOf("title" to title, "description" to "설명", "location" to "302동 208호"))
            }.andExpect { status { isCreated() } },
        )

    private fun createStudent(
        seminarId: Long,
        email: String = uniqueEmail(),
    ): Long =
        idOf(
            mockMvc.post("/students") {
                json(mapOf("name" to "학생", "age" to 20, "email" to email, "seminarId" to seminarId))
            }.andExpect { status { isCreated() } },
        )

    private fun createMeeting(
        seminarId: Long,
        capacity: Int,
    ): Long =
        idOf(
            mockMvc.post("/seminars/$seminarId/meetings") {
                json(
                    mapOf(
                        "title" to "1회차 모임",
                        "description" to "설명",
                        "scheduledAt" to Instant.parse("2026-09-29T10:00:00Z").toString(),
                        "capacity" to capacity,
                    ),
                )
            }.andExpect { status { isCreated() } },
        )

    private fun join(
        meetingId: Long,
        studentId: Long,
    ): ResultActionsDsl =
        mockMvc.post("/meetings/$meetingId/participations") {
            json(mapOf("studentId" to studentId))
        }

    private fun idOf(result: ResultActionsDsl): Long =
        objectMapper.readTree(result.andReturn().response.contentAsString).path("id").asLong()

    private fun uniqueEmail(): String = "student-${sequence.incrementAndGet()}@snu.ac.kr"

    private fun org.springframework.test.web.servlet.MockHttpServletRequestDsl.json(body: Any) {
        contentType = MediaType.APPLICATION_JSON
        content = objectMapper.writeValueAsString(body)
    }

    private companion object {
        val sequence = AtomicLong()
    }
}
