package com.wafflestudio.spring2026.data_access

import com.wafflestudio.spring2026.student.Student
import org.springframework.data.repository.Repository

/**
 * 3층. Spring Data JDBC — SQL 도 객체 변환도 프레임워크가 만든다. 선언만 남는다.
 *
 * `CrudRepository` 가 아니라 빈 [Repository] 를 상속해서, 여기 적은 메서드 하나만
 * 생기는 것을 보이게 했다. 실제로 쓰는 것은 `student/StudentRepository.kt` 이고
 * 이건 앞의 두 층과 나란히 놓고 비교하려고 둔 것이다.
 */
interface SpringDataStudentRepository : Repository<Student, Long> {
    fun findAllBySeminarId(seminarId: Long): List<Student>
}
