package com.wafflestudio.spring2026.data_access

import com.wafflestudio.spring2026.student.Student
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 * 2층. Spring JDBC (JdbcTemplate)
 *
 * 순수 JDBC 의 다섯 단계 중 **반복되는 네 개를 스프링이 대신 해 준다.**
 * 커넥션을 빌리고 돌려주는 것, Statement 와 ResultSet 을 닫는 것,
 * `SQLException`(체크 예외)을 런타임 예외로 바꾸는 것까지다.
 *
 * 남는 일은 두 가지뿐이다 — **SQL 을 쓰는 것**과 **한 줄을 객체로 옮기는 것**([RowMapper]).
 *
 * [JdbcTemplate] 빈은 스프링부트가 DataSource 를 보고 자동으로 만들어 둔다.
 * 주입만 받으면 된다.
 */
@Repository
class JdbcTemplateStudentRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    /**
     * [PlainJdbcStudentRepository.findAllBySeminarId] 와 같은 일을 한다.
     * try/finally 도, close 도, 예외 처리도 없다.
     */
    fun findAllBySeminarId(seminarId: Long): List<Student> =
        jdbcTemplate.query(
            "SELECT id, name, age, email, seminar_id, phone FROM students WHERE seminar_id = ?",
            studentRowMapper,
            seminarId,
        )

    /** 값 하나만 꺼낼 때는 `queryForObject` 를 쓴다. RowMapper 도 필요 없다. */
    fun countBySeminarId(seminarId: Long): Int =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM students WHERE seminar_id = ?",
            Int::class.java,
            seminarId,
        ) ?: 0

    /**
     * 메서드 이름으로는 만들 수 없는 쿼리를 직접 쓸 수 있다는 것이 이 층의 쓸모다.
     * 세미나별 학생 수를 한 번에 세는 것처럼, 여러 테이블을 묶거나 집계하는 쿼리에 쓴다.
     */
    fun countByEachSeminar(): Map<Long, Int> =
        jdbcTemplate
            .query("SELECT seminar_id, COUNT(*) AS student_count FROM students GROUP BY seminar_id") { rs, _ ->
                rs.getLong("seminar_id") to rs.getInt("student_count")
            }.toMap()

    private companion object {
        /** ResultSet 한 줄 -> 객체. 순수 JDBC 에서 손으로 쓰던 그 부분만 남은 것이다. */
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
