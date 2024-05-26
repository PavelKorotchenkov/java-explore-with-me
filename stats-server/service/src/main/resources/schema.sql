create TABLE IF NOT EXISTS stats (
    stats_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    app varchar(100) NOT NULL,
    uri varchar(100) NOT NULL,
    ip varchar(20) NOT NULL,
    created timestamp WITHOUT TIME ZONE
);

