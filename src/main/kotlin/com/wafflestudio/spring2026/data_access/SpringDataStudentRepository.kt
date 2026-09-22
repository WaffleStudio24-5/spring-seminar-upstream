package com.wafflestudio.spring2026.data_access

import com.wafflestudio.spring2026.student.Student
import org.springframework.data.repository.Repository

/**
 * 3층. Spring Data JDBC — SQL 도 객체 변환도 프레임워크가 한다. 메서드 이름만 남는다.
 *
 * 기본은 이것, 이름으로 표현이 안 되는 쿼리는 `@Query`,
 * 복잡한 집계나 성능이 중요한 자리는 [JdbcTemplateStudentRepository] 처럼 JdbcTemplate.
 *
 * 실제로 쓰는 것은 `student/StudentRepository.kt` 다. 이건 비교하려고 둔 것이다.
 */
interface SpringDataStudentRepository : Repository<Student, Long> {
    fun findAllBySeminarId(seminarId: Long): List<Student>
}
