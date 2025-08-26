CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255),
                       surname VARCHAR(255),
                       role role NOT NULL,
                       is_deleted BOOLEAN DEFAULT FALSE,
                       is_active BOOLEAN DEFAULT FALSE
);
