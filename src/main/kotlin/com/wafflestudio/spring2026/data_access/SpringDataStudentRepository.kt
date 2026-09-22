package com.wafflestudio.spring2026.data_access

import com.wafflestudio.spring2026.student.Student
import org.springframework.data.repository.Repository

/**
 * 3층. Spring Data JDBC
 *
 * SQL 과 객체 변환을 **둘 다** 프레임워크가 처리한다.
 * 남는 일은 메서드 이름을 짓는 것뿐이고, 구현체는 스프링이 만들어 빈으로 등록한다.
 *
 * 아래 한 줄이 앞의 두 층이 하던 일과 같은 결과를 낸다.
 *
 * ```
 * SELECT ... FROM students WHERE seminar_id = ?
 * ```
 *
 * ### 그래서 무엇을 쓰나
 *
 * | 상황 | 쓸 것 |
 * | --- | --- |
 * | 대부분 | Spring Data JDBC. 메서드 이름으로 끝난다 |
 * | 이름으로 표현이 안 되는 쿼리 (JOIN 등) | 같은 인터페이스에 `@Query` 로 SQL 을 직접 |
 * | 복잡한 집계, 성능이 중요한 자리 | [JdbcTemplateStudentRepository] 처럼 JdbcTemplate |
 * | 순수 JDBC | 실무에서 직접 쓸 일은 거의 없다. 아래 두 층이 무엇을 대신해 주는지 알아 두는 용도 |
 *
 * 실제로 쓰는 것은 `student/StudentRepository.kt` 다. 이건 나란히 놓고 비교하려고 둔 것이라
 * [Repository] 만 상속해서 필요한 메서드 하나만 갖는다. `CrudRepository` 를 상속하면
 * save·findById·findAll 같은 것이 함께 딸려 온다.
 */
interface SpringDataStudentRepository : Repository<Student, Long> {
    fun findAllBySeminarId(seminarId: Long): List<Student>
}
