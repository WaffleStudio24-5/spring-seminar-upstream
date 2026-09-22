package com.wafflestudio.spring2026.practice.repository

import com.wafflestudio.spring2026.practice.model.Participation
import com.wafflestudio.spring2026.practice.model.Student
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface ParticipationRepository : CrudRepository<Participation, Long> {
    fun findByStudentIdAndMeetingId(
        studentId: Long,
        meetingId: Long,
    ): Participation?

    fun countByMeetingId(meetingId: Long): Int

    /**
     * 메서드 이름으로는 만들 수 없는 쿼리는 `@Query` 에 SQL 을 직접 쓴다.
     *
     * 참여자 목록을 얻으려면 participations 에서 학생 ID 를 꺼내 students 를 다시 읽어야 하는데,
     * 두 번 나눠 읽는 대신 JOIN 으로 한 번에 가져온다.
     * `SELECT s.*` 로 students 의 칼럼만 고르면 결과가 [Student] 로 그대로 매핑된다.
     */
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
