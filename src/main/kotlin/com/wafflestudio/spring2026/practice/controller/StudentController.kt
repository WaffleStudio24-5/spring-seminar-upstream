package com.wafflestudio.spring2026.practice.controller

import com.wafflestudio.spring2026.practice.dto.StudentCreateRequest
import com.wafflestudio.spring2026.practice.dto.StudentResponse
import com.wafflestudio.spring2026.practice.service.StudentService
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
@RequestMapping("/practice/students")
class StudentController(
    private val studentService: StudentService,
) {
    @PostMapping
    fun createStudent(
        @Valid @RequestBody request: StudentCreateRequest,
    ): ResponseEntity<StudentResponse> {
        val student =
            studentService.createStudent(request.name, request.age, request.email, request.seminarId)

        return ResponseEntity
            .created(URI.create("/practice/students/${student.requireId()}"))
            .body(StudentResponse.from(student))
    }

    @GetMapping("/{id}")
    fun getStudent(
        @PathVariable id: Long,
    ): StudentResponse = StudentResponse.from(studentService.getStudent(id))
}
