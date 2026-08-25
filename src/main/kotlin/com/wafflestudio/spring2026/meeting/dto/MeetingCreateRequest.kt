package com.wafflestudio.spring2026.meeting.dto

data class MeetingCreateRequest(
    val title: String,
    val capacity: Int,
)
