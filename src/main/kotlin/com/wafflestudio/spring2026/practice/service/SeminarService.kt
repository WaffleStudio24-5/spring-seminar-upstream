package com.wafflestudio.spring2026.practice.service

import com.wafflestudio.spring2026.practice.model.Seminar
import com.wafflestudio.spring2026.practice.model.SeminarNotFoundException
import com.wafflestudio.spring2026.practice.repository.SeminarRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class SeminarService(
    private val seminarRepository: SeminarRepository,
) {
    fun createSeminar(
        title: String,
        description: String?,
        location: String,
    ): Seminar = seminarRepository.save(Seminar(title = title, description = description, location = location))

    fun getSeminar(id: Long): Seminar = seminarRepository.findByIdOrNull(id) ?: throw SeminarNotFoundException(id)

    fun getSeminarList(): List<Seminar> = seminarRepository.findAll().toList()
}
