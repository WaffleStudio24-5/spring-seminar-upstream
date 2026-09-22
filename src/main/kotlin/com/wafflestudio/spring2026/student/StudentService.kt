package com.wafflestudio.spring2026.student

import com.wafflestudio.spring2026.seminar.SeminarService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val seminarService: SeminarService,
) {
    fun createStudent(
        name: String,
        age: Int,
        email: String,
        seminarId: Long,
        phone: String?,
    ): Student {
        seminarService.getSeminar(seminarId)

        if (studentRepository.existsByEmail(email)) {
            throw EmailAlreadyExistsException(email)
        }

        return studentRepository.save(
            Student(name = name, age = age, email = email, seminarId = seminarId, phone = phone),
        )
    }

    fun getStudent(id: Long): Student = studentRepository.findByIdOrNull(id) ?: throw StudentNotFoundException(id)

    fun getStudentsOfSeminar(seminarId: Long): List<Student> {
        seminarService.getSeminar(seminarId)

        return studentRepository.findAllBySeminarId(seminarId)
    }
}
