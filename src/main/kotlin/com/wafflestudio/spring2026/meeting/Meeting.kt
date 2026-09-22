package com.wafflestudio.spring2026.meeting

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

/** 세미나가 여는 모임. 한 세미나에 여러 개가 있을 수 있다. */
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
