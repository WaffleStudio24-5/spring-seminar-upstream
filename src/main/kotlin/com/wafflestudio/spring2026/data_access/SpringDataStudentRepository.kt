package com.wafflestudio.spring2026.data_access

import com.wafflestudio.spring2026.student.Student
import org.springframework.data.repository.Repository

interface SpringDataStudentRepository : Repository<Student, Long> {
    fun findAllBySeminarId(seminarId: Long): List<Student>
}
