DROP TABLE IF EXISTS loan_publications;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS membership;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS profiles;
DROP TABLE IF EXISTS publication;


CREATE TABLE profiles (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          bio VARCHAR(255) NOT NULL,
                          location VARCHAR(255) NOT NULL,
                          interests VARCHAR(255) NOT NULL
);

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       age INT NOT NULL,
                       profile_id BIGINT,
                       FOREIGN KEY (profile_id) REFERENCES profiles(id)
);

CREATE TABLE publication (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             title VARCHAR(255) NOT NULL,
                             publication_year INT NOT NULL,
                             available_copies INT NOT NULL,
                             publication_type VARCHAR(31) NOT NULL,
                             author VARCHAR(255),
                             isbn VARCHAR(255),
                             editor VARCHAR(255),
                             issn VARCHAR(255)
);

CREATE TABLE membership (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            start_date DATE NOT NULL,
                            end_date DATE NOT NULL,
                            type VARCHAR(255) NOT NULL,
                            user_email VARCHAR(255) NOT NULL,
                            free_loans  INT NOT NULL DEFAULT 0,
                            FOREIGN KEY (user_email) REFERENCES users(email)
);

CREATE TABLE loans (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       start_date DATE NOT NULL,
                       end_date DATE NOT NULL,
                       user_email VARCHAR(255) NOT NULL,
                       returned BOOLEAN DEFAULT FALSE NOT NULL,
                       price DECIMAL(8,2) DEFAULT 0 NOT NULL,
                       FOREIGN KEY (user_email) REFERENCES users(email) ON DELETE CASCADE
);

CREATE TABLE loan_publications (
                                   loan_id BIGINT NOT NULL,
                                   publication_id BIGINT NOT NULL,
                                   PRIMARY KEY (loan_id, publication_id),
                                   FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE,
                                   FOREIGN KEY (publication_id) REFERENCES publication(id) ON DELETE CASCADE
);