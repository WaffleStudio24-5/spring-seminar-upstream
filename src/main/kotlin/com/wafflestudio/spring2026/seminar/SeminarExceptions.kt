package com.wafflestudio.spring2026.seminar

import com.wafflestudio.spring2026.ApiException
import org.springframework.http.HttpStatus

class SeminarNotFoundException(id: Long) :
    ApiException(HttpStatus.NOT_FOUND, "SEMINAR_NOT_FOUND", "ID가 ${id}인 세미나를 찾을 수 없습니다.")
