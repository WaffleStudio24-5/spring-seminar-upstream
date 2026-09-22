package com.wafflestudio.spring2026.meeting

import com.wafflestudio.spring2026.student.StudentResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class MeetingController(
    private val meetingService: MeetingService,
) {
    @PostMapping("/seminars/{seminarId}/meetings")
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
            .created(URI.create("/meetings/${meeting.requireId()}"))
            .body(MeetingResponse.of(meeting, participantCount = 0))
    }

    @GetMapping("/seminars/{seminarId}/meetings")
    fun getMeetingList(
        @PathVariable seminarId: Long,
    ): List<MeetingResponse> =
        meetingService.getMeetingsOfSeminar(seminarId).map { meeting ->
            MeetingResponse.of(meeting, meetingService.countParticipants(meeting.requireId()))
        }

    @GetMapping("/meetings/{id}")
    fun getMeeting(
        @PathVariable id: Long,
    ): MeetingResponse = MeetingResponse.of(meetingService.getMeeting(id), meetingService.countParticipants(id))

    @GetMapping("/meetings/{id}/participants")
    fun getParticipants(
        @PathVariable id: Long,
    ): List<StudentResponse> = meetingService.getParticipants(id).map(StudentResponse::from)

    @PostMapping("/meetings/{id}/participations")
    fun join(
        @PathVariable id: Long,
        @RequestBody request: ParticipationRequest,
    ): ResponseEntity<List<StudentResponse>> {
        meetingService.join(meetingId = id, studentId = request.studentId)

        return ResponseEntity
            .created(URI.create("/meetings/$id/participants"))
            .body(meetingService.getParticipants(id).map(StudentResponse::from))
    }

    @DeleteMapping("/meetings/{id}/participations/{studentId}")
    fun leave(
        @PathVariable id: Long,
        @PathVariable studentId: Long,
    ): ResponseEntity<Void> {
        meetingService.leave(meetingId = id, studentId = studentId)

        return ResponseEntity.noContent().build()
    }
}
