CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    original_text TEXT NOT NULL,
    language VARCHAR(2) NOT NULL,
    corrected_text TEXT,
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);