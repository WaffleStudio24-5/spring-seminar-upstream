package com.wafflestudio.spring2026.practice.dto

import com.wafflestudio.spring2026.practice.model.Meeting
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.Instant

data class MeetingCreateRequest(
    @field:NotBlank(message = "모임 제목은 비어 있을 수 없습니다.")
    val title: String,
    val description: String? = null,
    val scheduledAt: Instant,
    @field:Positive(message = "정원은 1명 이상이어야 합니다.")
    val capacity: Int,
)

data class MeetingResponse(
    val id: Long,
    val seminarId: Long,
    val title: String,
    val description: String?,
    val scheduledAt: Instant,
    val capacity: Int,
    /** 지금 몇 명이 참여했는지. 저장하지 않고 participations 를 세어서 구한다. */
    val participantCount: Int,
) {
    companion object {
        fun of(
            meeting: Meeting,
            participantCount: Int,
        ): MeetingResponse =
            MeetingResponse(
                id = meeting.requireId(),
                seminarId = meeting.seminarId,
                title = meeting.title,
                description = meeting.description,
                scheduledAt = meeting.scheduledAt,
                capacity = meeting.capacity,
                participantCount = participantCount,
            )
    }
}

data class ParticipationRequest(
    val studentId: Long,
)
