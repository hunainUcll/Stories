DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS profiles;

CREATE TABLE profiles (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          bio VARCHAR(255) NOT NULL,
                          location VARCHAR(255) NOT NULL,
                          interests VARCHAR(255) NOT NULL
);
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(25),
                       age INT,
                       email VARCHAR(255),
                       password VARCHAR(25),
    profile_id BIGINT,
    FOREIGN KEY(profile_id) REFERENCES profiles
);