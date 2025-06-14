-- liquibase formatted sql

-- changeset mkorolkov:1
CREATE TABLE ad_entities (
    pk INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    image VARCHAR(100) NOT NULL,
    price INTEGER NOT NULL,
    title VARCHAR(100) NOT NULL,
    author INT NOT NULL,
    FOREIGN KEY (author) REFERENCES user_entities(id)
);