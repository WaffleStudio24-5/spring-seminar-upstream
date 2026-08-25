package com.wafflestudio.spring2026.meeting.service

import com.wafflestudio.spring2026.meeting.model.Meeting
import com.wafflestudio.spring2026.meeting.repository.MeetingRepository
import org.springframework.stereotype.Service

@Service
class MeetingService(
    private val meetingRepository: MeetingRepository,
) {
    fun createMeeting(
        title: String,
        capacity: Int,
    ): Meeting =
        meetingRepository.save(
            title = title,
            capacity = capacity,
        )

    fun findMeeting(id: Long): Meeting? = meetingRepository.findById(id)
}
