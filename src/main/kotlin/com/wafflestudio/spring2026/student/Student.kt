package com.wafflestudio.spring2026.student

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * 학생. 세미나 하나에 속한다.
 *
 * 관계는 [seminarId] 하나로 표현한다. 테이블의 FK 칼럼이 그대로 필드가 된 것이다.
 * 필드 이름 `seminarId` 는 칼럼 이름 `seminar_id` 로 자동 변환된다.
 */
@Table("students")
data class Student(
    @Id
    val id: Long? = null,
    val name: String,
    val age: Int,
    val email: String,
    val seminarId: Long,
    /** V2 에서 뒤늦게 추가한 칼럼이라, 그전에 등록한 학생에게는 값이 없다. */
    val phone: String? = null,
) {
    fun requireId(): Long = requireNotNull(id) { "아직 저장되지 않은 학생입니다." }
}
