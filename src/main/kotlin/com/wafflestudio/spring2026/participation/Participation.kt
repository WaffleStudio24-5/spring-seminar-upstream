package com.wafflestudio.spring2026.participation

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("participations")
data class Participation(
    @Id
    val id: Long? = null,
    val studentId: Long,
    val meetingId: Long,
    val joinedAt: Instant,
)
