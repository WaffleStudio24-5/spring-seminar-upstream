package com.wafflestudio.spring2026.practice.model

import org.springframework.http.HttpStatus

/**
 * 이 예제가 내보내는 오류.
 *
 * 예외마다 상태 코드와 오류 코드를 자기가 들고 있으면,
 * 기능이 늘어도 예외를 변환하는 곳은 한 군데로 유지된다.
 */
abstract class PracticeException(
    val status: HttpStatus,
    val code: String,
    override val message: String,
) : RuntimeException(message)

class SeminarNotFoundException(id: Long) :
    PracticeException(HttpStatus.NOT_FOUND, "SEMINAR_NOT_FOUND", "ID가 ${id}인 세미나를 찾을 수 없습니다.")

class StudentNotFoundException(id: Long) :
    PracticeException(HttpStatus.NOT_FOUND, "STUDENT_NOT_FOUND", "ID가 ${id}인 학생을 찾을 수 없습니다.")

class MeetingNotFoundException(id: Long) :
    PracticeException(HttpStatus.NOT_FOUND, "MEETING_NOT_FOUND", "ID가 ${id}인 모임을 찾을 수 없습니다.")

class EmailAlreadyExistsException(email: String) :
    PracticeException(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", "이미 ${email}로 등록한 학생이 있습니다.")

/** 학생은 자신이 속하지 않은 세미나의 모임에는 참여할 수 없다. */
class NotInSameSeminarException(studentId: Long, meetingId: Long) :
    PracticeException(
        HttpStatus.CONFLICT,
        "NOT_IN_SAME_SEMINAR",
        "학생 ${studentId}는 모임 ${meetingId}의 세미나에 속해 있지 않습니다.",
    )

class AlreadyParticipatingException(studentId: Long, meetingId: Long) :
    PracticeException(
        HttpStatus.CONFLICT,
        "ALREADY_PARTICIPATING",
        "학생 ${studentId}는 이미 모임 ${meetingId}에 참여했습니다.",
    )

class MeetingFullException(meetingId: Long, capacity: Int) :
    PracticeException(HttpStatus.CONFLICT, "MEETING_FULL", "모임 ${meetingId}의 정원 ${capacity}명이 찼습니다.")

class NotParticipatingException(studentId: Long, meetingId: Long) :
    PracticeException(
        HttpStatus.NOT_FOUND,
        "PARTICIPATION_NOT_FOUND",
        "학생 ${studentId}는 모임 ${meetingId}에 참여하지 않았습니다.",
    )
