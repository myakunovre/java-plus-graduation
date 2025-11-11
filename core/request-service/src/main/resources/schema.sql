CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id     BIGINT                                          NOT NULL,
    requester_id BIGINT                                          NOT NULL,
    status       VARCHAR DEFAULT 'PENDING'                       NOT NULL CHECK ( status IN ('PENDING', 'REJECTED', 'CONFIRMED', 'CANCELED')),
    created      TIMESTAMP                                       NOT NULL
);