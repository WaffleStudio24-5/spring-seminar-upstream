package com.wafflestudio.spring2026.student

import org.springframework.data.repository.CrudRepository

interface StudentRepository : CrudRepository<Student, Long> {
    /** 메서드 이름이 곧 쿼리다. → `WHERE seminar_id = ?` */
    fun findAllBySeminarId(seminarId: Long): List<Student>
    /*
    * select * from students where seminar_id = ${seminar_id}
    *
    *
    * */


    fun existsByEmail(email: String): Boolean
}
