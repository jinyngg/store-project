CREATE TABLE member (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        email VARCHAR(255) NOT NULL,
                        phone VARCHAR(20) NOT NULL UNIQUE,
                        nickname VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        member_status VARCHAR(20) NOT NULL,
                        member_role VARCHAR(20) NOT NULL,
                        registered_at DATETIME,
                        unregistered_at DATETIME,
                        created_at DATETIME NOT NULL,
                        updated_at DATETIME NOT NULL,
                        CONSTRAINT uc_member_email UNIQUE (email)
);

CREATE TABLE store (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       member_id BIGINT NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       address VARCHAR(255) NOT NULL,
                       description VARCHAR(500) NOT NULL,
                       store_status VARCHAR(20) NOT NULL,
                       lat DOUBLE NOT NULL,
                       lon DOUBLE NOT NULL,
                       review_count INT,
                       average_rating DOUBLE,
                       business_hours VARCHAR(100),
                       break_time VARCHAR(100),
                       out_of_business_at DATETIME,
                       created_at DATETIME NOT NULL,
                       updated_at DATETIME NOT NULL,
                       CONSTRAINT fk_store_member FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE reservation (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             store_id BIGINT NOT NULL,
                             member_id BIGINT NOT NULL,
                             reservation_date DATE,
                             reservation_time VARCHAR(10),
                             reservation_memo VARCHAR(255),
                             number_of_customer INT,
                             reservation_code VARCHAR(4),
                             reservation_visit_status VARCHAR(50),
                             reservation_approval_status VARCHAR(20),
                             created_at DATETIME NOT NULL,
                             updated_at DATETIME NOT NULL,
                             CONSTRAINT fk_reservation_store FOREIGN KEY (store_id) REFERENCES store (id),
                             CONSTRAINT fk_reservation_member FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE review (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        store_id BIGINT NOT NULL,
                        member_id BIGINT NOT NULL,
                        reservation_id BIGINT NOT NULL,
                        message VARCHAR(255),
                        rating FLOAT,
                        review_status VARCHAR(20),
                        visited_date DATE,
                        created_at DATETIME NOT NULL,
                        updated_at DATETIME NOT NULL,
                        CONSTRAINT fk_review_store FOREIGN KEY (store_id) REFERENCES store (id),
                        CONSTRAINT fk_review_member FOREIGN KEY (member_id) REFERENCES member (id),
                        CONSTRAINT fk_review_reservation FOREIGN KEY (reservation_id) REFERENCES reservation (id)
);

CREATE TABLE refresh_token (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               created_at DATETIME NOT NULL,
                               updated_at DATETIME,
                               member_id BIGINT NOT NULL,
                               token VARCHAR(255) NOT NULL
);
