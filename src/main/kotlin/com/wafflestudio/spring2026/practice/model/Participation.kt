package com.wafflestudio.spring2026.practice.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

/**
 * 학생이 모임에 참여한 기록.
 *
 * 학생과 모임은 다대다인데, 다대다는 테이블로 바로 표현할 수 없다.
 * 그래서 이 중간 테이블을 두어 학생-참여(일대다), 참여-모임(다대일)로 바꾸어 표현한다.
 * 참여 자체에 딸린 값(`joinedAt`)도 여기에 둔다.
 */
@Table("participations")
data class Participation(
    @Id
    val id: Long? = null,
    val studentId: Long,
    val meetingId: Long,
    val joinedAt: Instant,
)
