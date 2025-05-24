DROP TABLE IF EXISTS membership;
DROP TABLE IF EXISTS publication;
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

CREATE TABLE membership (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            start_date DATE NOT NULL,
                            end_date DATE NOT NULL,
                            membership_type VARCHAR(10),
                            user_id BIGINT NOT NULL,
                            CONSTRAINT fk_membership_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE publication (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             title VARCHAR(255) NOT NULL,
                             publication_year INT NOT NULL,
                             available_copies INT NOT NULL,
                             author VARCHAR(255),
                             editor VARCHAR(255),
                             isbn VARCHAR(255),
                             issn VARCHAR(255),
                             type VARCHAR(255) NOT NULL
);
