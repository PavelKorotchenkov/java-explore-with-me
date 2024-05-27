create TABLE IF NOT EXISTS events(
    event_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    event_annotation varchar(200),
    category_id BIGINT,
    event_confirmed_requests BIGINT,
    event_created_on timestamp WITHOUT TIME ZONE,
    event_description varchar(1000),
    event_date timestamp WITHOUT TIME ZONE,
    initiator_id BIGINT,
    location_id BIGINT,
    event_paid boolean,
    event_participant_limit int,
    event_published_on timestamp WITHOUT TIME ZONE,
    event_request_moderation boolean,
    event_state varchar(10),
    event_title varchar(50),
    CONSTRAINT fk_events_to_category FOREIGN KEY(category_id) REFERENCES categories(category_id) ON delete CASCADE,
    CONSTRAINT fk_events_to_users FOREIGN KEY(initiator_id) REFERENCES users(user_id) ON delete CASCADE,
    CONSTRAINT fk_events_to_locations FOREIGN KEY(location_id) REFERENCES locations(location_id) ON delete CASCADE
);

create TABLE IF NOT EXISTS categories(
    category_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE NOT NULL,
    category_name varchar(50)
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

CREATE OR REPLACE FUNCTION distance(lat1 float, lon1 float, lat2 float, lon2 float)
    RETURNS float
AS
'
declare
    dist float = 0;
    rad_lat1 float;
    rad_lat2 float;
    theta float;
    rad_theta float;
BEGIN
    IF lat1 = lat2 AND lon1 = lon2
    THEN
        RETURN dist;
    ELSE
        -- ��������� ������� ������ � �������
        rad_lat1 = pi() * lat1 / 180;
        -- ��������� ������� ������� � �������
        rad_lat2 = pi() * lat2 / 180;
        -- ������� �������� ������
        theta = lon1 - lon2;
        -- ��������� ������� � �������
        rad_theta = pi() * theta / 180;
        -- ������� ����� ����������
        dist = sin(rad_lat1) * sin(rad_lat2) + cos(rad_lat1) * cos(rad_lat2) * cos(rad_theta);

        IF dist > 1
            THEN dist = 1;
        END IF;

        dist = acos(dist);
        -- ��������� ������� � �������
        dist = dist * 180 / pi();
        -- ��������� ������� � ���������
        dist = dist * 60 * 1.8524;

        RETURN dist;
    END IF;
END;
'
LANGUAGE PLPGSQL;