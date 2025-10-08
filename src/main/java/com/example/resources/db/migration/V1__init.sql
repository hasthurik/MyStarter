CREATE TABLE error_log (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    method_signature VARCHAR(500) NOT NULL,
    exception_message VARCHAR(500),
    stacktrace TEXT,
    args_json TEXT
);