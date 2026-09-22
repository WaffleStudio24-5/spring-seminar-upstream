package com.wafflestudio.spring2026.practice.service

import com.wafflestudio.spring2026.practice.model.AlreadyParticipatingException
import com.wafflestudio.spring2026.practice.model.Meeting
import com.wafflestudio.spring2026.practice.model.MeetingFullException
import com.wafflestudio.spring2026.practice.model.MeetingNotFoundException
import com.wafflestudio.spring2026.practice.model.NotInSameSeminarException
import com.wafflestudio.spring2026.practice.model.NotParticipatingException
import com.wafflestudio.spring2026.practice.model.Participation
import com.wafflestudio.spring2026.practice.model.Student
import com.wafflestudio.spring2026.practice.repository.MeetingRepository
import com.wafflestudio.spring2026.practice.repository.ParticipationRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

// 1주차 과제의 MeetingService 와 이름이 겹쳐 빈 이름을 직접 지어 준다.
@Service("practiceMeetingService")
@Transactional(readOnly = true)
class MeetingService(
    private val meetingRepository: MeetingRepository,
    private val participationRepository: ParticipationRepository,
    private val seminarService: SeminarService,
    private val studentService: StudentService,
) {
    @Transactional
    fun createMeeting(
        seminarId: Long,
        title: String,
        description: String?,
        scheduledAt: Instant,
        capacity: Int,
    ): Meeting {
        seminarService.getSeminar(seminarId)

        return meetingRepository.save(
            Meeting(
                seminarId = seminarId,
                title = title,
                description = description,
                scheduledAt = scheduledAt,
                capacity = capacity,
            ),
        )
    }

    fun getMeeting(id: Long): Meeting = meetingRepository.findByIdOrNull(id) ?: throw MeetingNotFoundException(id)

    fun getMeetingsOfSeminar(seminarId: Long): List<Meeting> {
        seminarService.getSeminar(seminarId)

        return meetingRepository.findAllBySeminarIdOrderByScheduledAt(seminarId)
    }

    fun countParticipants(meetingId: Long): Int = participationRepository.countByMeetingId(meetingId)

    fun getParticipants(meetingId: Long): List<Student> {
        getMeeting(meetingId)

        return participationRepository.findParticipantsByMeetingId(meetingId)
    }

    /**
     * 학생이 모임에 참여한다.
     *
     * 요구사항의 핵심 규칙이 여기에 있다 —
     * **학생은 자신이 속하지 않은 세미나의 모임에는 참여할 수 없다.**
     * 이건 FK 로는 표현할 수 없어서(두 FK 가 서로 다른 테이블을 가리킨다) 코드가 확인한다.
     */
    @Transactional
    fun join(
        meetingId: Long,
        studentId: Long,
    ): Participation {
        val meeting = getMeeting(meetingId)
        val student = studentService.getStudent(studentId)

        if (student.seminarId != meeting.seminarId) {
            throw NotInSameSeminarException(studentId, meetingId)
        }
        if (participationRepository.findByStudentIdAndMeetingId(studentId, meetingId) != null) {
            throw AlreadyParticipatingException(studentId, meetingId)
        }
        if (participationRepository.countByMeetingId(meetingId) >= meeting.capacity) {
            throw MeetingFullException(meetingId, meeting.capacity)
        }

        return participationRepository.save(
            Participation(studentId = studentId, meetingId = meetingId, joinedAt = Instant.now()),
        )
    }

    @Transactional
    fun leave(
        meetingId: Long,
        studentId: Long,
    ) {
        val participation =
            participationRepository.findByStudentIdAndMeetingId(studentId, meetingId)
                ?: throw NotParticipatingException(studentId, meetingId)

        participationRepository.delete(participation)
    }
}
