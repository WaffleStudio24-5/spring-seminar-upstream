package com.wafflestudio.spring2026.meeting.controller

import com.wafflestudio.spring2026.meeting.dto.MeetingCreateRequest
import com.wafflestudio.spring2026.meeting.dto.MeetingResponse
import com.wafflestudio.spring2026.meeting.service.MeetingService
import java.net.URI
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/meetings")
class MeetingController(
    private val meetingService: MeetingService,
) {
    @PostMapping
    fun createMeeting(
        @RequestBody request: MeetingCreateRequest,
    ): ResponseEntity<MeetingResponse> {
        val meeting = meetingService.createMeeting(
            title = request.title,
            capacity = request.capacity,
        )

        val response = MeetingResponse.from(meeting)

        return ResponseEntity
            .created(URI.create("/meetings/${meeting.id}"))
            .body(response)
    }

    @GetMapping("/{id}")
    fun getMeeting(
        @PathVariable("id") id: Long,
    ): ResponseEntity<MeetingResponse> {
        val meeting = meetingService.findMeeting(id)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(
            MeetingResponse.from(meeting),
        )
    }
}
