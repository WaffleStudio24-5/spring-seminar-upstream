package com.wafflestudio.spring2026.student

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

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
