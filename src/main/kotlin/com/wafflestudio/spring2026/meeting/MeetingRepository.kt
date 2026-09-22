package com.wafflestudio.spring2026.meeting

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface MeetingRepository : CrudRepository<Meeting, Long> {
    fun findAllBySeminarIdOrderByScheduledAt(seminarId: Long): List<Meeting>
}
