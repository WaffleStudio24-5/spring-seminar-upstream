package com.wafflestudio.spring2026.seminar

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("seminars")
data class Seminar(
    @Id
    val id: Long? = null,
    val title: String,
    val description: String?,
    val location: String,
) {
    fun requireId(): Long = requireNotNull(id) { "아직 저장되지 않은 세미나입니다." }
}
