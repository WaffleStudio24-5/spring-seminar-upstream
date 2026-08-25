package com.wafflestudio.spring2026.meeting

class MeetingNotFoundException(
    val meetingId: Long,
) : RuntimeException(
        "ID가 ${meetingId}인 모임을 찾을 수 없습니다.",
    )
