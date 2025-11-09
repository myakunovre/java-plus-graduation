CREATE TABLE IF NOT EXISTS comments
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text            VARCHAR(7000)                   NOT NULL,
    event_id        BIGINT                          NOT NULL,
    author_id       BIGINT                          NOT NULL,
    created_on      TIMESTAMP                       NOT NULL,
    published_on    TIMESTAMP,
    modified_on     TIMESTAMP,
    state           VARCHAR DEFAULT 'PENDING'       NOT NULL CHECK (state IN ('PENDING', 'PUBLISHED', 'CANCELED'))
);