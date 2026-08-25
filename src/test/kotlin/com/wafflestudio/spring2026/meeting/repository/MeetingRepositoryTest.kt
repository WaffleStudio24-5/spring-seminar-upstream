package com.wafflestudio.spring2026.meeting.repository

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class MeetingRepositoryTest {
    @Test
    fun `saves meetings with sequential ids`() {
        val repository = MeetingRepository()

        val first = repository.save("Spring 스터디", 10)
        val second = repository.save("Kotlin 스터디", 15)

        assertEquals(1L, first.id)
        assertEquals(2L, second.id)
        assertSame(first, repository.findById(first.id))
        assertNull(repository.findById(999L))
    }
}
