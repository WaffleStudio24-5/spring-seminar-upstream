package com.wafflestudio.spring2026.student

import org.springframework.data.repository.CrudRepository

interface StudentRepository : CrudRepository<Student, Long> {
    /** 메서드 이름이 곧 쿼리다. → `WHERE seminar_id = ?` */
    fun findAllBySeminarId(seminarId: Long): List<Student>

    fun existsByEmail(email: String): Boolean
}
