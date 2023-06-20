CREATE TABLE member (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        email VARCHAR(100) NOT NULL,
                        phone VARCHAR(20) NOT NULL UNIQUE,
                        nickname VARCHAR(50) NOT NULL,
                        password VARCHAR(100) NOT NULL,
                        member_status VARCHAR(20) NOT NULL,
                        member_role VARCHAR(20) NOT NULL,
                        registered_at DATETIME,
                        unregistered_at DATETIME
);

CREATE TABLE store (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       name VARCHAR(255) NOT NULL,
                       address VARCHAR(255) NOT NULL,
                       description VARCHAR(255) NOT NULL,
                       store_status VARCHAR(20) NOT NULL,
                       lat DOUBLE PRECISION NOT NULL,
                       lon DOUBLE PRECISION NOT NULL,
                       owner_phone VARCHAR(20) NOT NULL,
                       out_of_business_at DATETIME,
                       CONSTRAINT fk_owner FOREIGN KEY (owner_phone) REFERENCES member(phone)
);