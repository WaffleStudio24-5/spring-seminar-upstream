package com.wafflestudio.spring2026.student

import com.wafflestudio.spring2026.ApiException
import org.springframework.http.HttpStatus

class StudentNotFoundException(id: Long) :
    ApiException(HttpStatus.NOT_FOUND, "STUDENT_NOT_FOUND", "ID가 ${id}인 학생을 찾을 수 없습니다.")

class EmailAlreadyExistsException(email: String) :
    ApiException(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", "이미 ${email}로 등록한 학생이 있습니다.")
