package com.wafflestudio.spring2026.meeting

import com.wafflestudio.spring2026.participation.AlreadyParticipatingException
import com.wafflestudio.spring2026.participation.NotInSameSeminarException
import com.wafflestudio.spring2026.participation.NotParticipatingException
import com.wafflestudio.spring2026.participation.Participation
import com.wafflestudio.spring2026.participation.ParticipationRepository
import com.wafflestudio.spring2026.seminar.SeminarService
import com.wafflestudio.spring2026.student.Student
import com.wafflestudio.spring2026.student.StudentService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
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

    /** 학생은 자신이 속하지 않은 세미나의 모임에는 참여할 수 없다. */
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
