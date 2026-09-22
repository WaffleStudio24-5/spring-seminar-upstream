package com.wafflestudio.spring2026.meeting

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("meetings")
data class Meeting(
    @Id
    val id: Long? = null,
    val seminarId: Long,
    val title: String,
    val description: String?,
    val scheduledAt: Instant,
    val capacity: Int,
) {
    fun requireId(): Long = requireNotNull(id) { "아직 저장되지 않은 모임입니다." }
}
