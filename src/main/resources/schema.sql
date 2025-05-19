DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(25),
                       age INT,
                       email VARCHAR(255),
                       password VARCHAR(25)
);