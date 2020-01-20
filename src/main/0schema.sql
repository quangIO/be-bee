CREATE TABLE IF NOT EXISTS bee_status
(
    bee_id    serial PRIMARY KEY,
    speed     float     NOT NULL DEFAULT 0,
    latitude  float,
    longitude float,
    elevation float,
    fuel      float,
    damage    float,
    modified  timestamp NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS status_event
(
    id        serial PRIMARY KEY,
    bee_id    int       NOT NULL references bee_status (bee_id) ON DELETE CASCADE,
    speed     float,
    latitude  float,
    longitude float,
    elevation float,
    fuel      float,
    damage    float,
    modified  timestamp NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS nectar_event
(
    id       serial PRIMARY KEY,
    bee_id   int       NOT NULL references bee_status (bee_id) ON DELETE CASCADE,
    amount   float     NOT NULL,
    modified timestamp NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS honey_event
(
    id       serial PRIMARY KEY,
    bee_id   int       NOT NULL references bee_status (bee_id) ON DELETE CASCADE,
    amount   float     NOT NULL,
    modified timestamp NOT NULL DEFAULT now()
);


