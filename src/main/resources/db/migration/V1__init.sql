-- 수업 Practice 1 에서 설계한 테이블.

CREATE TABLE seminars
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NULL,
    location    VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE students
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    age        INT          NOT NULL,
    email      VARCHAR(255) NOT NULL,
    -- 학생은 세미나 하나에만 속한다. 그래서 학생 쪽에 FK 를 하나 둔다.
    -- 부모-자식 관계에서 FK 는 자식 쪽에 둔다.
    seminar_id BIGINT       NOT NULL,
    PRIMARY KEY (id),
    -- 같은 이메일로 두 번 가입할 수 없다. 조회도 이 인덱스를 탄다.
    UNIQUE KEY uk_students_email (email),
    CONSTRAINT fk_students_seminar FOREIGN KEY (seminar_id) REFERENCES seminars (id)
) ENGINE = InnoDB;

CREATE TABLE meetings
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    seminar_id   BIGINT       NOT NULL,
    title        VARCHAR(255) NOT NULL,
    description  TEXT         NULL,
    scheduled_at DATETIME(6)  NOT NULL,
    capacity     INT          NOT NULL,
    PRIMARY KEY (id),
    KEY idx_meetings_seminar (seminar_id),
    CONSTRAINT fk_meetings_seminar FOREIGN KEY (seminar_id) REFERENCES seminars (id)
) ENGINE = InnoDB;

CREATE TABLE participations
(
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    student_id BIGINT      NOT NULL,
    meeting_id BIGINT      NOT NULL,
    joined_at  DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    -- 같은 학생이 같은 모임에 두 번 참여할 수 없다. 서비스 코드가 아니라 DB 가 막는다.
    UNIQUE KEY uk_participations_student_meeting (student_id, meeting_id),
    KEY idx_participations_meeting (meeting_id),
    CONSTRAINT fk_participations_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_participations_meeting FOREIGN KEY (meeting_id) REFERENCES meetings (id)
) ENGINE = InnoDB;
