package com.wafflestudio.spring2026.meeting.dto

import com.wafflestudio.spring2026.meeting.model.Meeting

data class MeetingResponse(
    val id: Long,
    val title: String,
    val capacity: Int,
) {
    companion object {
        fun from(meeting: Meeting): MeetingResponse =
            MeetingResponse(
                id = meeting.id,
                title = meeting.title,
                capacity = meeting.capacity,
            )
    }
}
