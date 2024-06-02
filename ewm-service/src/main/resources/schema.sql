drop table if exists categories cascade;
drop table if exists events cascade;
drop table if exists participation_requests cascade;
drop table if exists locations cascade;
drop table if exists users cascade;
drop table if exists compilations cascade;
drop table if exists compilation_events cascade;

create TABLE IF NOT EXISTS categories(
    category_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    category_name varchar(50) UNIQUE NOT NULL
);

create TABLE IF NOT EXISTS users(
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    user_email varchar(254),
    user_name varchar(250)
);

create TABLE IF NOT EXISTS locations(
    location_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    lat float,
    lon float
);

create TABLE IF NOT EXISTS events(
    event_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    event_annotation varchar(2000),
    category_id BIGINT,
    event_confirmed_requests BIGINT,
    event_created_on timestamp WITHOUT TIME ZONE,
    event_description varchar(7000),
    event_date timestamp WITHOUT TIME ZONE,
    initiator_id BIGINT,
    location_id BIGINT,
    event_paid boolean,
    event_participant_limit int,
    event_published_on timestamp WITHOUT TIME ZONE,
    event_request_moderation boolean,
    event_state varchar(10),
    event_title varchar(120),
    CONSTRAINT fk_events_to_categories FOREIGN KEY(category_id) REFERENCES categories(category_id) ON DELETE CASCADE,
    CONSTRAINT fk_events_to_users FOREIGN KEY(initiator_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_events_to_locations FOREIGN KEY(location_id) REFERENCES locations(location_id) ON DELETE CASCADE
);

create TABLE IF NOT EXISTS participation_requests(
    participation_request_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    created timestamp WITHOUT TIME ZONE,
    event_id BIGINT,
    requester_id BIGINT,
    status varchar(10),
    CONSTRAINT fk_participation_requests_to_events FOREIGN KEY(event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT fk_participation_requests_to_users FOREIGN KEY(requester_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uq_request UNIQUE(event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS compilations (
    compilation_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pinned boolean,
    title VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT,
    event_id BIGINT,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations(compilation_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE
);