CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_name CHARACTER varying (80) NOT NULL,
    user_email CHARACTER varying(120) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT uq_users_email UNIQUE (user_email)
);

CREATE TABLE IF NOT EXISTS categories
(
    category_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    category_name CHARACTER varying(120) NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (category_id),
    CONSTRAINT uq_categories_name UNIQUE (category_name)
);

CREATE TABLE IF NOT EXISTS locations
(
    location_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    latitude NUMERIC(9, 6) NOT NULL,
    longitude NUMERIC(9, 6) NOT NULL,
    CONSTRAINT pk_locations PRIMARY KEY (location_id)
);

CREATE TABLE IF NOT EXISTS states
(
    state_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    state_name CHARACTER varying(20) NOT NULL,
    CONSTRAINT pk_states PRIMARY KEY (state_id),
    CONSTRAINT uq_states_name UNIQUE (state_name)
);

CREATE TABLE IF NOT EXISTS events
(
    event_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    event_title CHARACTER varying(500) NOT NULL NOT NULL,
    description CHARACTER varying(5000),
    annotation CHARACTER varying(2000) NOT NULL,
    category_id BIGINT NOT NULL NOT NULL,
    initiator_id BIGINT NOT NULL NOT NULL,
    location_id BIGINT NOT NULL NOT NULL,
    state_id BIGINT NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    paid BOOLEAN NOT NULL,
    confirmed_requests BIGINT,
    participant_limit INTEGER,
    views BIGINT,
    request_moderation BOOLEAN,
    CONSTRAINT pk_events PRIMARY KEY (event_id),
    CONSTRAINT fk_category_id FOREIGN KEY (category_id)
        REFERENCES categories (category_id),
    CONSTRAINT fk_initiator_id FOREIGN KEY (initiator_id)
        REFERENCES users (user_id),
    CONSTRAINT fk_location_id FOREIGN KEY (location_id)
        REFERENCES locations (location_id),
    CONSTRAINT fk_state_id FOREIGN KEY (state_id)
        REFERENCES states (state_id)
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id   BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    event_id     BIGINT NOT NULL NOT NULL,
    requester_id BIGINT NOT NULL NOT NULL,
    status_id    BIGINT NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_requests PRIMARY KEY (request_id),
    CONSTRAINT fk_requester_id FOREIGN KEY (requester_id)
        REFERENCES users (user_id),
    CONSTRAINT fk_status_id FOREIGN KEY (status_id)
        REFERENCES states (state_id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    compilation_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    pinned BOOLEAN NOT NULL,
    compilation_title CHARACTER varying(500) NOT NULL NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (compilation_id),
    CONSTRAINT uq_title_compilations UNIQUE (compilation_title)
);

CREATE TABLE IF NOT EXISTS events_compilations
(
    event_id BIGINT REFERENCES events (event_id) ON DELETE CASCADE,
    compilation_id BIGINT REFERENCES compilations (compilation_id) ON DELETE CASCADE
);

ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE categories ALTER COLUMN category_id RESTART WITH 1;
ALTER TABLE locations ALTER COLUMN location_id RESTART WITH 1;
ALTER TABLE states ALTER COLUMN state_id RESTART WITH 1;
ALTER TABLE events ALTER COLUMN event_id RESTART WITH 1;
ALTER TABLE requests ALTER COLUMN request_id RESTART WITH 1;
ALTER TABLE compilations ALTER COLUMN compilation_id RESTART WITH 1;

DELETE FROM users;
DELETE FROM categories;
DELETE FROM locations;
DELETE FROM states;
DELETE FROM events;
DELETE FROM requests;
DELETE FROM compilations;
