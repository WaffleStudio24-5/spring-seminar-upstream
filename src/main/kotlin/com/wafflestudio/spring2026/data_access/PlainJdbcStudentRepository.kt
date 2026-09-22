package com.wafflestudio.spring2026.data_access

import com.wafflestudio.spring2026.student.Student
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.sql.DriverManager

/**
 * 1층. 순수 JDBC — 자바가 RDB 와 통신하는 표준 API.
 *
 * 커넥션을 열고, 쿼리하고, ResultSet 을 객체로 옮기고, 닫는다. 전부 직접 한다.
 * `use` 는 블록을 벗어날 때 `close()` 를 불러 준다.
 */
@Repository
class PlainJdbcStudentRepository(
    @Value("\${spring.datasource.url}") private val url: String,
    @Value("\${spring.datasource.username}") private val username: String,
    @Value("\${spring.datasource.password}") private val password: String,
) {
    fun findAllBySeminarId(seminarId: Long): List<Student> {
        val sql = "SELECT id, name, age, email, seminar_id, phone FROM students WHERE seminar_id = ?"

        DriverManager.getConnection(url, username, password).use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setLong(1, seminarId)
                statement.executeQuery().use { resultSet ->
                    return buildList {
                        while (resultSet.next()) {
                            add(
                                Student(
                                    id = resultSet.getLong("id"),
                                    name = resultSet.getString("name"),
                                    age = resultSet.getInt("age"),
                                    email = resultSet.getString("email"),
                                    seminarId = resultSet.getLong("seminar_id"),
                                    phone = resultSet.getString("phone"),
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
