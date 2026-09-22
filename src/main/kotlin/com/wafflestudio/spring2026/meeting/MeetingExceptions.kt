package com.wafflestudio.spring2026.meeting

import com.wafflestudio.spring2026.ApiException
import org.springframework.http.HttpStatus

class MeetingNotFoundException(id: Long) :
    ApiException(HttpStatus.NOT_FOUND, "MEETING_NOT_FOUND", "ID가 ${id}인 모임을 찾을 수 없습니다.")

class MeetingFullException(meetingId: Long, capacity: Int) :
    ApiException(HttpStatus.CONFLICT, "MEETING_FULL", "모임 ${meetingId}의 정원 ${capacity}명이 찼습니다.")
