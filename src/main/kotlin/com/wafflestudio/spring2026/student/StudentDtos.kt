package com.wafflestudio.spring2026.student

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class StudentCreateRequest(
    @field:NotBlank(message = "이름은 비어 있을 수 없습니다.")
    val name: String,
    @field:Positive(message = "나이는 1 이상이어야 합니다.")
    val age: Int,
    @field:Email(message = "이메일 형식이 아닙니다.")
    val email: String,
    val seminarId: Long,
)

data class StudentResponse(
    val id: Long,
    val name: String,
    val age: Int,
    val email: String,
    val seminarId: Long,
) {
    companion object {
        fun from(student: Student): StudentResponse =
            StudentResponse(
                id = student.requireId(),
                name = student.name,
                age = student.age,
                email = student.email,
                seminarId = student.seminarId,
            )
    }
}
