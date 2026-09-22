package com.wafflestudio.spring2026.student

import com.wafflestudio.spring2026.seminar.SeminarService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val seminarService: SeminarService,
) {
    /**
     * 학생은 반드시 세미나 하나에 속한다.
     *
     * 없는 세미나를 가리키면 DB 의 FK 제약이 막아 주지만,
     * 그 전에 확인해서 "세미나를 찾을 수 없다"는 쓸 만한 메시지를 돌려준다.
     */
    fun createStudent(
        name: String,
        age: Int,
        email: String,
        seminarId: Long,
    ): Student {
        seminarService.getSeminar(seminarId)

        if (studentRepository.existsByEmail(email)) {
            throw EmailAlreadyExistsException(email)
        }

        return studentRepository.save(Student(name = name, age = age, email = email, seminarId = seminarId))
    }

    fun getStudent(id: Long): Student = studentRepository.findByIdOrNull(id) ?: throw StudentNotFoundException(id)

    fun getStudentsOfSeminar(seminarId: Long): List<Student> {
        seminarService.getSeminar(seminarId)

        return studentRepository.findAllBySeminarId(seminarId)
    }
}
