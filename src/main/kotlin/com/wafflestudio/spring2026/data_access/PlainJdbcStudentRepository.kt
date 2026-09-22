package com.wafflestudio.spring2026.data_access

import com.wafflestudio.spring2026.student.Student
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.sql.DriverManager
import java.sql.ResultSet
import javax.sql.DataSource

/**
 * 1층. 순수 JDBC
 *
 * JDBC 는 자바 프로그램이 RDB 와 통신하기 위한 **표준 API** 다.
 * 드라이버(`com.mysql:mysql-connector-j`)가 이 표준을 MySQL 프로토콜로 옮긴다.
 *
 * 다음 다섯 단계를 매번 직접 밟아야 한다.
 *
 *   1. Connection 을 얻는다
 *   2. SQL 로 PreparedStatement 를 만든다
 *   3. `?` 자리에 값을 순서대로 채운다
 *   4. ResultSet 을 한 줄씩 돌며 객체로 옮긴다
 *   5. **셋 다 닫는다** — 안 닫으면 커넥션이 새어 나가 결국 DB 가 더 받지 못한다
 *
 * 아래 두 메서드는 1번(커넥션을 어디서 얻는가)만 다르다. 나머지는 똑같다.
 */
@Repository
class PlainJdbcStudentRepository(
    private val dataSource: DataSource,
    @Value("\${spring.datasource.url}") private val url: String,
    @Value("\${spring.datasource.username}") private val username: String,
    @Value("\${spring.datasource.password}") private val password: String,
) {
    /**
     * 커넥션 풀에서 빌려 쓴다.
     *
     * [DataSource] 는 스프링부트가 만들어 둔 HikariCP 풀이다. `getConnection()` 은
     * 새 연결을 맺는 것이 아니라 **미리 만들어 둔 것을 빌려 온다**. 그래서 `close()` 도
     * 연결을 끊는 게 아니라 풀에 돌려주는 일이다. 그래도 반드시 닫아야 한다.
     *
     * Kotlin 의 `use` 는 블록을 벗어날 때 — 예외가 나도 — `close()` 를 불러 준다.
     * `use` 가 없으면 아래 [countWithoutPool] 처럼 `finally` 에 직접 써야 한다.
     */
    fun findAllBySeminarId(seminarId: Long): List<Student> {
        val sql = "SELECT id, name, age, email, seminar_id, phone FROM students WHERE seminar_id = ?"

        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setLong(1, seminarId)
                statement.executeQuery().use { resultSet ->
                    buildList {
                        while (resultSet.next()) {
                            add(resultSet.toStudent())
                        }
                    }
                }
            }
        }
    }

    /**
     * 풀 없이, 쿼리 한 번마다 연결을 새로 맺고 끊는다.
     *
     * [DriverManager.getConnection] 은 그때그때 **TCP 연결과 인증을 새로 한다**.
     * 이게 수 ms 씩 드는데, 정작 쿼리 자체보다 오래 걸리는 일이 흔하다.
     * 커넥션 풀이 있는 이유가 이것이다. 실제 코드에서는 이렇게 쓰지 않는다.
     *
     * 닫는 과정을 눈으로 보려고 `use` 대신 `try`/`finally` 로 썼다.
     */
    fun countWithoutPool(seminarId: Long): Int {
        val connection = DriverManager.getConnection(url, username, password)
        try {
            val statement = connection.prepareStatement("SELECT COUNT(*) FROM students WHERE seminar_id = ?")
            try {
                statement.setLong(1, seminarId)
                val resultSet = statement.executeQuery()
                try {
                    return if (resultSet.next()) resultSet.getInt(1) else 0
                } finally {
                    resultSet.close()
                }
            } finally {
                statement.close()
            }
        } finally {
            // 이걸 빠뜨리면 커넥션이 그대로 남는다. 반복되면 DB 의 연결 수 한도에 걸린다.
            connection.close()
        }
    }

    /** ResultSet 의 한 줄을 객체로 옮긴다. 칼럼 이름과 타입을 하나씩 직접 적어 준다. */
    private fun ResultSet.toStudent(): Student =
        Student(
            id = getLong("id"),
            name = getString("name"),
            age = getInt("age"),
            email = getString("email"),
            seminarId = getLong("seminar_id"),
            phone = getString("phone"),
        )
}
