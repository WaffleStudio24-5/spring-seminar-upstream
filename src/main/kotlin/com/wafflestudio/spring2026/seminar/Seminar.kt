package com.wafflestudio.spring2026.seminar

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * 세미나. spring, frontend, fastapi … 처럼 여러 개가 있다.
 *
 * `@Table` 로 어느 테이블에 대응하는지, `@Id` 로 어느 필드가 PK 인지 알려 주면
 * Spring Data JDBC 가 객체와 레코드 사이를 옮겨 준다.
 * 아직 저장하지 않은 객체는 id 가 null 이고, 저장하면 DB 가 붙여 준 값이 채워진다.
 */
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
