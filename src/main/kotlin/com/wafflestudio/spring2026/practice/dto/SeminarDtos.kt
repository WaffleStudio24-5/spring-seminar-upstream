package com.wafflestudio.spring2026.practice.dto

import com.wafflestudio.spring2026.practice.model.Seminar
import jakarta.validation.constraints.NotBlank

data class SeminarCreateRequest(
    @field:NotBlank(message = "세미나 제목은 비어 있을 수 없습니다.")
    val title: String,
    val description: String? = null,
    @field:NotBlank(message = "장소는 비어 있을 수 없습니다.")
    val location: String,
)

data class SeminarResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val location: String,
) {
    companion object {
        fun from(seminar: Seminar): SeminarResponse =
            SeminarResponse(
                id = seminar.requireId(),
                title = seminar.title,
                description = seminar.description,
                location = seminar.location,
            )
    }
}
