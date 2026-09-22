package com.wafflestudio.spring2026.di

fun main() {
    // 미리 객체를 생성하고, 의존성을 주입해야 합니다.
    val meetingRepository = ExampleMeetingRepository()
    val meetingService = ExampleMeetingService(meetingRepository)
    val meetingController = ExampleMeetingController(meetingService)

    // API 요청이 다음 순서대로 온다고 해봅시다.
    // POST /meetings
    val springStudy = meetingController.createMeeting(
        title = "Spring Study",
        capacity = 10,
    )

    // POST /meetings
    val kotlinStudy = meetingController.createMeeting(
        title = "Kotlin Study",
        capacity = 8,
    )

    // GET /meetings
    meetingController.getMeetingList()

    // GET /meetings/{id}
    meetingController.getMeeting(springStudy.id)

    // PATCH /meetings/{id}
    meetingController.patchMeeting(
        id = kotlinStudy.id,
        title = "Advanced Kotlin Study",
        capacity = 12,
    )

    // DELETE /meetings/{id}
    meetingController.deleteMeeting(springStudy.id)

    // GET /meetings
    meetingController.getMeetingList()
}

class ExampleMeetingController(
    private val meetingService: ExampleMeetingService,
) {
    fun createMeeting(
        title: String,
        capacity: Int,
    ): ExampleMeeting {
        val meeting = meetingService.createMeeting(
            title = title,
            capacity = capacity,
        )
        println("201 Created: $meeting")
        return meeting
    }

    fun getMeeting(id: Long): ExampleMeeting {
        val meeting = meetingService.getMeeting(id)
        println("200 OK: $meeting")
        return meeting
    }

    fun getMeetingList(): List<ExampleMeeting> {
        val meetings = meetingService.getMeetingList()
        println("200 OK: $meetings")
        return meetings
    }

    fun patchMeeting(
        id: Long,
        title: String?,
        capacity: Int?,
    ): ExampleMeeting {
        val meeting = meetingService.patchMeeting(
            id = id,
            title = title,
            capacity = capacity,
        )
        println("200 OK: $meeting")
        return meeting
    }

    fun deleteMeeting(id: Long) {
        meetingService.deleteMeeting(id)
        println("204 No Content")
    }
}

class ExampleMeetingService(
    private val meetingRepository: ExampleMeetingRepository,
) {
    fun createMeeting(
        title: String,
        capacity: Int,
    ): ExampleMeeting =
        meetingRepository.save(
            title = title,
            capacity = capacity,
        )

    fun getMeeting(id: Long): ExampleMeeting =
        meetingRepository.findById(id)
            ?: throw IllegalArgumentException("Meeting not found. id=$id")

    fun getMeetingList(): List<ExampleMeeting> =
        meetingRepository.findAll()

    fun patchMeeting(
        id: Long,
        title: String?,
        capacity: Int?,
    ): ExampleMeeting {
        val existingMeeting = getMeeting(id)

        return meetingRepository.update(
            id = id,
            title = title ?: existingMeeting.title,
            capacity = capacity ?: existingMeeting.capacity,
        )
    }

    fun deleteMeeting(id: Long) {
        meetingRepository.deleteById(id)
    }
}

class ExampleMeetingRepository {
    private val meetings = mutableMapOf<Long, ExampleMeeting>()
    private var nextId = 1L

    fun save(
        title: String,
        capacity: Int,
    ): ExampleMeeting {
        val meeting = ExampleMeeting(
            id = nextId,
            title = title,
            capacity = capacity,
        )

        nextId += 1
        meetings[meeting.id] = meeting

        return meeting
    }

    fun findById(id: Long): ExampleMeeting? =
        meetings[id]

    fun findAll(): List<ExampleMeeting> =
        meetings.values.toList()

    fun update(
        id: Long,
        title: String,
        capacity: Int,
    ): ExampleMeeting {
        val meeting = ExampleMeeting(
            id = id,
            title = title,
            capacity = capacity,
        )

        meetings[id] = meeting

        return meeting
    }

    fun deleteById(id: Long) {
        meetings.remove(id)
    }
}

data class ExampleMeeting(
    val id: Long,
    val title: String,
    val capacity: Int,
)
