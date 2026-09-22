package com.wafflestudio.spring2026.seminar

import com.wafflestudio.spring2026.student.StudentResponse
import com.wafflestudio.spring2026.student.StudentService
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
@RequestMapping("/seminars")
class SeminarController(
    private val seminarService: SeminarService,
    private val studentService: StudentService,
) {
    @PostMapping
    fun createSeminar(
        @Valid @RequestBody request: SeminarCreateRequest,
    ): ResponseEntity<SeminarResponse> {
        val seminar = seminarService.createSeminar(request.title, request.description, request.location)

        return ResponseEntity
            .created(URI.create("/seminars/${seminar.requireId()}"))
            .body(SeminarResponse.from(seminar))
    }

    @GetMapping
    fun getSeminarList(): List<SeminarResponse> = seminarService.getSeminarList().map(SeminarResponse::from)

    @GetMapping("/{id}")
    fun getSeminar(
        @PathVariable id: Long,
    ): SeminarResponse = SeminarResponse.from(seminarService.getSeminar(id))

    @GetMapping("/{id}/students")
    fun getStudents(
        @PathVariable id: Long,
    ): List<StudentResponse> = studentService.getStudentsOfSeminar(id).map(StudentResponse::from)
}
