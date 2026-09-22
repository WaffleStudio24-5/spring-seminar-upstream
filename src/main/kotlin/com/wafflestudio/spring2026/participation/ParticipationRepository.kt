package com.wafflestudio.spring2026.participation

import com.wafflestudio.spring2026.student.Student
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface ParticipationRepository : CrudRepository<Participation, Long> {
    fun findByStudentIdAndMeetingId(
        studentId: Long,
        meetingId: Long,
    ): Participation?

    fun countByMeetingId(meetingId: Long): Int

    /** JOIN 은 메서드 이름으로 표현할 수 없다. 이럴 때 `@Query` 에 SQL 을 직접 쓴다. */
    @Query(
        """
        SELECT s.*
        FROM participations p
        JOIN students s ON s.id = p.student_id
        WHERE p.meeting_id = :meetingId
        ORDER BY p.joined_at, s.id
        """,
    )
    fun findParticipantsByMeetingId(
        @Param("meetingId") meetingId: Long,
    ): List<Student>
}
