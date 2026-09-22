package com.wafflestudio.spring2026.practice.repository

import com.wafflestudio.spring2026.practice.model.Seminar
import org.springframework.data.repository.CrudRepository

/**
 * `CrudRepository` 를 상속하기만 하면 save, findById, findAll, deleteById 등이
 * 구현 없이 생긴다. 구현체는 Spring Data JDBC 가 만들어 빈으로 등록한다.
 */
interface SeminarRepository : CrudRepository<Seminar, Long>
