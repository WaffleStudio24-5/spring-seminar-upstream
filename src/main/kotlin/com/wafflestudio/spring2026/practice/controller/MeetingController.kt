package com.wafflestudio.spring2026.practice.controller

import com.wafflestudio.spring2026.practice.dto.MeetingCreateRequest
import com.wafflestudio.spring2026.practice.dto.MeetingResponse
import com.wafflestudio.spring2026.practice.dto.ParticipationRequest
import com.wafflestudio.spring2026.practice.dto.StudentResponse
import com.wafflestudio.spring2026.practice.service.MeetingService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

// 1주차 과제의 MeetingController 와 클래스 이름이 같다.
// 빈 이름은 클래스 이름에서 만들어지므로 그대로 두면 충돌한다. 그래서 직접 지어 준다.
@RestController("practiceMeetingController")
class MeetingController(
    private val meetingService: MeetingService,
) {
    @PostMapping("/practice/seminars/{seminarId}/meetings")
    fun createMeeting(
        @PathVariable seminarId: Long,
        @Valid @RequestBody request: MeetingCreateRequest,
    ): ResponseEntity<MeetingResponse> {
        val meeting =
            meetingService.createMeeting(
                seminarId = seminarId,
                title = request.title,
                description = request.description,
                scheduledAt = request.scheduledAt,
                capacity = request.capacity,
            )

        return ResponseEntity
            .created(URI.create("/practice/meetings/${meeting.requireId()}"))
            .body(MeetingResponse.of(meeting, participantCount = 0))
    }

    @GetMapping("/practice/seminars/{seminarId}/meetings")
    fun getMeetingList(
        @PathVariable seminarId: Long,
    ): List<MeetingResponse> =
        meetingService.getMeetingsOfSeminar(seminarId).map { meeting ->
            MeetingResponse.of(meeting, meetingService.countParticipants(meeting.requireId()))
        }

    @GetMapping("/practice/meetings/{id}")
    fun getMeeting(
        @PathVariable id: Long,
    ): MeetingResponse = MeetingResponse.of(meetingService.getMeeting(id), meetingService.countParticipants(id))

    @GetMapping("/practice/meetings/{id}/participants")
    fun getParticipants(
        @PathVariable id: Long,
    ): List<StudentResponse> = meetingService.getParticipants(id).map(StudentResponse::from)

    @PostMapping("/practice/meetings/{id}/participations")
    fun join(
        @PathVariable id: Long,
        @RequestBody request: ParticipationRequest,
    ): ResponseEntity<List<StudentResponse>> {
        meetingService.join(meetingId = id, studentId = request.studentId)

        return ResponseEntity
            .created(URI.create("/practice/meetings/$id/participants"))
            .body(meetingService.getParticipants(id).map(StudentResponse::from))
    }

    @DeleteMapping("/practice/meetings/{id}/participations/{studentId}")
    fun leave(
        @PathVariable id: Long,
        @PathVariable studentId: Long,
    ): ResponseEntity<Void> {
        meetingService.leave(meetingId = id, studentId = studentId)

        return ResponseEntity.noContent().build()
    }
}
