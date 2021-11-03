CREATE TABLE IF NOT EXISTS guild
(
    id           TEXT PRIMARY KEY,
    created      TIMESTAMPTZ DEFAULT now(),
    last_updated TIMESTAMPTZ NULL,
    prefix       TEXT NOT NULL
);
