package com.wafflestudio.spring2026.seminar

import org.springframework.data.repository.CrudRepository

/** `CrudRepository` 만 상속해도 save·findById·findAll·deleteById 가 구현 없이 생긴다. */
interface SeminarRepository : CrudRepository<Seminar, Long>
