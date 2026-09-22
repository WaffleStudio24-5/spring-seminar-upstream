package com.wafflestudio.spring2026.participation

import com.wafflestudio.spring2026.ApiException
import org.springframework.http.HttpStatus

/** 학생은 자신이 속하지 않은 세미나의 모임에는 참여할 수 없다. */
class NotInSameSeminarException(studentId: Long, meetingId: Long) :
    ApiException(
        HttpStatus.CONFLICT,
        "NOT_IN_SAME_SEMINAR",
        "학생 ${studentId}는 모임 ${meetingId}의 세미나에 속해 있지 않습니다.",
    )

class AlreadyParticipatingException(studentId: Long, meetingId: Long) :
    ApiException(
        HttpStatus.CONFLICT,
        "ALREADY_PARTICIPATING",
        "학생 ${studentId}는 이미 모임 ${meetingId}에 참여했습니다.",
    )

class NotParticipatingException(studentId: Long, meetingId: Long) :
    ApiException(
        HttpStatus.NOT_FOUND,
        "PARTICIPATION_NOT_FOUND",
        "학생 ${studentId}는 모임 ${meetingId}에 참여하지 않았습니다.",
    )
