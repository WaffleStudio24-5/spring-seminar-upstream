package com.wafflestudio.spring2026.practice.repository

import com.wafflestudio.spring2026.practice.model.Meeting
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

// 1주차 과제의 MeetingRepository 와 이름이 겹쳐 빈 이름을 직접 지어 준다.
@Repository("practiceMeetingRepository")
interface MeetingRepository : CrudRepository<Meeting, Long> {
    fun findAllBySeminarIdOrderByScheduledAt(seminarId: Long): List<Meeting>
}
