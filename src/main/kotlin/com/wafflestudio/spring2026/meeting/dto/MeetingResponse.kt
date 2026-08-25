package com.wafflestudio.spring2026.meeting.dto

data class MeetingResponse(
    val id: Long,
    val title: String,
    val capacity: Int,
)
