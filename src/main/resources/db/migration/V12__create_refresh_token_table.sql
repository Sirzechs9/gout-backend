CREATE TABLE IF NOT EXISTS "refresh_token" (
    id SERIAL PRIMARY KEY,
    token VARCHAR(4000) NOT NULL,
    issued_date TIMESTAMP WITH TIME ZONE NOT NULL,
    usage VARCHAR(30) NOT NULL,
    resource_id INTEGER NOT NULL,
    is_expired BOOLEAN NOT NULL
);
