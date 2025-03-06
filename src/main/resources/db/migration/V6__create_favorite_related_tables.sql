CREATE TABLE IF NOT EXISTS "favorite" (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    tour_id INTEGER NOT NULL REFERENCES "tour"(id)
);