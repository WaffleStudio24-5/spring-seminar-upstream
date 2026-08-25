package com.wafflestudio.spring2026.meeting.controller

import com.wafflestudio.spring2026.meeting.dto.MeetingResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/meetings")
class MeetingController {
    @GetMapping("/{id}")
    fun getMeeting(
        @PathVariable("id") id: Long,
    ): ResponseEntity<MeetingResponse> {
        val response = MeetingResponse(
            id = id,
            title = "Spring 스터디",
            capacity = 10,
        )

        return ResponseEntity.ok(response)
    }
}
