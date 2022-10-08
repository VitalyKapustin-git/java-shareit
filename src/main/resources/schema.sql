CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name varchar not null,
    email varchar not null,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT unique_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description varchar not null,
    requestor_id bigint not null,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_requestor FOREIGN KEY (requestor_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name varchar not null,
    description varchar not null,
    is_available boolean not null,
    owner_id bigint not null,
    request_id bigint,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT fk_request FOREIGN KEY (request_id) REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE not null,
    end_date TIMESTAMP WITHOUT TIME ZONE not null,
    item_id bigint not null,
    booker_id bigint,
    approve_status varchar,
    CONSTRAINT pk_booking PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text varchar not null,
    item_id bigint not null,
    author_id bigint,
    created TIMESTAMP WITHOUT TIME ZONE not null,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

