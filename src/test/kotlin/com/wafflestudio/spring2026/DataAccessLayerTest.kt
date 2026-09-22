package com.wafflestudio.spring2026

import com.wafflestudio.spring2026.data_access.JdbcTemplateStudentRepository
import com.wafflestudio.spring2026.data_access.PlainJdbcStudentRepository
import com.wafflestudio.spring2026.data_access.SpringDataStudentRepository
import com.wafflestudio.spring2026.seminar.Seminar
import com.wafflestudio.spring2026.seminar.SeminarRepository
import com.wafflestudio.spring2026.student.Student
import com.wafflestudio.spring2026.student.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.atomic.AtomicLong
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
class DataAccessLayerTest(
    @Autowired private val plainJdbc: PlainJdbcStudentRepository,
    @Autowired private val jdbcTemplate: JdbcTemplateStudentRepository,
    @Autowired private val springData: SpringDataStudentRepository,
    @Autowired private val seminarRepository: SeminarRepository,
    @Autowired private val studentRepository: StudentRepository,
) {
    @Test
    fun `세 계층이 같은 학생 목록을 돌려준다`() {
        val seminarId = givenSeminarWithStudents(count = 3)

        val fromPlainJdbc = plainJdbc.findAllBySeminarId(seminarId)
        val fromJdbcTemplate = jdbcTemplate.findAllBySeminarId(seminarId)
        val fromSpringData = springData.findAllBySeminarId(seminarId)

        assertEquals(3, fromPlainJdbc.size)
        assertEquals(fromPlainJdbc.sortedBy { it.id }, fromJdbcTemplate.sortedBy { it.id })
        assertEquals(fromPlainJdbc.sortedBy { it.id }, fromSpringData.sortedBy { it.id })
    }

    @Test
    fun `JdbcTemplate 으로 학생 수를 센다`() {
        val seminarId = givenSeminarWithStudents(count = 2)

        assertEquals(2, jdbcTemplate.countBySeminarId(seminarId))
        assertEquals(2, jdbcTemplate.countByEachSeminar()[seminarId])
    }

    private fun givenSeminarWithStudents(count: Int): Long {
        val seminar =
            seminarRepository.save(Seminar(title = unique("layer-세미나"), description = null, location = "302동"))
        val seminarId = seminar.requireId()

        repeat(count) {
            studentRepository.save(
                Student(name = unique("학생"), age = 20, email = "${unique("layer")}@snu.ac.kr", seminarId = seminarId),
            )
        }

        return seminarId
    }

    private fun unique(prefix: String): String = "$prefix-${sequence.incrementAndGet()}"

    private companion object {
        val sequence = AtomicLong()
    }
}
