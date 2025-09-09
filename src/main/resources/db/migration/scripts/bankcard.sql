-- liquibase formatted sql
-- changeset andrey-rock:1

CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(64)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL UNIQUE,
    role     VARCHAR(16)  NOT NULL,
    enabled  BOOLEAN      NOT NULL DEFAULT true
);

CREATE TABLE cards
(
    card_id         uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    card_number     VARCHAR(255) NOT NULL UNIQUE,
    owner_id        BIGINT      NOT NULL,
    expiration_date DATE        NOT NULL,
    status          VARCHAR(16) NOT NULL,
    balance         NUMERIC check (balance >= 0),
    FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);