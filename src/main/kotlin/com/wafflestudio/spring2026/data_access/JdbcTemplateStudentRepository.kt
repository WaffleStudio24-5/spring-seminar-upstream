package com.wafflestudio.spring2026.data_access

import com.wafflestudio.spring2026.student.Student
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 * 2층. Spring JDBC — 커넥션과 자원 정리, 예외 변환을 [JdbcTemplate] 이 대신한다.
 *
 * 남는 것은 SQL 과, 한 줄을 객체로 옮기는 [RowMapper] 뿐이다.
 */
@Repository
class JdbcTemplateStudentRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun findAllBySeminarId(seminarId: Long): List<Student> =
        jdbcTemplate.query(
            "SELECT id, name, age, email, seminar_id, phone FROM students WHERE seminar_id = ?",
            studentRowMapper,
            seminarId,
        )

    /** 값 하나만 꺼낼 때는 RowMapper 도 필요 없다. */
    fun countBySeminarId(seminarId: Long): Int =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM students WHERE seminar_id = ?",
            Int::class.java,
            seminarId,
        ) ?: 0

    /** 메서드 이름으로는 만들 수 없는 쿼리를 직접 쓸 수 있다는 것이 이 층의 쓸모다. */
    fun countByEachSeminar(): Map<Long, Int> =
        jdbcTemplate
            .query("SELECT seminar_id, COUNT(*) AS student_count FROM students GROUP BY seminar_id") { rs, _ ->
                rs.getLong("seminar_id") to rs.getInt("student_count")
            }.toMap()

    private companion object {
        val studentRowMapper = RowMapper { rs, _ ->
            Student(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                age = rs.getInt("age"),
                email = rs.getString("email"),
                seminarId = rs.getLong("seminar_id"),
                phone = rs.getString("phone"),
            )
        }
    }
}
