package com.wafflestudio.spring2026.student

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/students")
class StudentController(
    private val studentService: StudentService,
) {
    @PostMapping
    fun createStudent(
        @Valid @RequestBody request: StudentCreateRequest,
    ): ResponseEntity<StudentResponse> {
        val student =
            studentService.createStudent(
                name = request.name,
                age = request.age,
                email = request.email,
                seminarId = request.seminarId,
                phone = request.phone,
            )

        return ResponseEntity
            .created(URI.create("/students/${student.requireId()}"))
            .body(StudentResponse.from(student))
    }

    @GetMapping("/{id}")
    fun getStudent(
        @PathVariable id: Long,
    ): StudentResponse = StudentResponse.from(studentService.getStudent(id))
}
