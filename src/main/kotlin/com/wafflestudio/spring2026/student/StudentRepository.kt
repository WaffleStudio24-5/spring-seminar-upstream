package com.wafflestudio.spring2026.student

import org.springframework.data.repository.CrudRepository

interface StudentRepository : CrudRepository<Student, Long> {
    /**
     * 메서드 이름만으로 쿼리가 만들어진다.
     * `findAllBySeminarId` → `SELECT * FROM students WHERE seminar_id = ?`
     */
    fun findAllBySeminarId(seminarId: Long): List<Student>

    fun existsByEmail(email: String): Boolean
}
