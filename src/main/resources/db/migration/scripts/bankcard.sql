-- liquibase formatted sql
-- changeset andrey-rock:1

CREATE TABLE cards
(
    wallet_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    Card_number VARCHAR(19) not null,
    owner VARCHAR(32) not null,
    expiration_date DATE not null ,
    status VARCHAR(10) not null ,
    balance BIGINT check (balance > 0)
);