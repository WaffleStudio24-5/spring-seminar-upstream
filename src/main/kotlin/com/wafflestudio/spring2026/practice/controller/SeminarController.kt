package com.wafflestudio.spring2026.practice.controller

import com.wafflestudio.spring2026.practice.dto.SeminarCreateRequest
import com.wafflestudio.spring2026.practice.dto.SeminarResponse
import com.wafflestudio.spring2026.practice.dto.StudentResponse
import com.wafflestudio.spring2026.practice.service.SeminarService
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

/**
 * 1주차 과제의 `/meetings` 와 경로가 겹치지 않도록 `/practice` 아래에 둔다.
 */
@RestController
@RequestMapping("/practice/seminars")
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
            .created(URI.create("/practice/seminars/${seminar.requireId()}"))
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
