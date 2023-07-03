
## Development Environment

- <img src="https://img.shields.io/badge/Windows-blue?style=flat&logo=windows&logoColor=white"/> 
- <img src="https://img.shields.io/badge/intellij-red?style=flat&logo=intellijidea&logoColor=white"/> 
- <img src="https://img.shields.io/badge/JDK_11-red?style=flat&logo=&logoColor=white"/>
- <img src="https://img.shields.io/badge/MySQL-blue?style=flat&logo=mysql&logoColor=white"/>
- <img src="https://img.shields.io/badge/Gradle-blue?style=flat&logo=gradle&logoColor=white"/>
- <img src="https://img.shields.io/badge/Github-grey?style=flat&logo=github&logoColor=white"/>

## Dependencies
- ````Spring Web````
- ````Spring Data JPA````
- ````Spring Validation````
- ````Spring Security````
- ````MySql Database````
- ````Lombok````
- ````jsonwebtoken````

```java
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'org.springframework.boot:spring-boot-starter-security'
	implementation group: 'io.jsonwebtoken', name: 'jjwt', version: '0.9.1'

	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'com.mysql:mysql-connector-j'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testImplementation 'org.springframework.security:spring-security-test'
}
```

## DB

### DDL

```sql
CREATE TABLE member
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    email           VARCHAR(255) NOT NULL,
    phone           VARCHAR(20)  NOT NULL UNIQUE,
    nickname        VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    member_status   VARCHAR(20)  NOT NULL,
    member_role     VARCHAR(20)  NOT NULL,
    registered_at   DATETIME,
    unregistered_at DATETIME,
    created_at      DATETIME     NOT NULL,
    updated_at      DATETIME     NOT NULL,
    CONSTRAINT uc_member_email UNIQUE (email)
);


CREATE TABLE store
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id          BIGINT       NOT NULL,
    name               VARCHAR(100) NOT NULL,
    address            VARCHAR(255) NOT NULL,
    description        VARCHAR(500) NOT NULL,
    store_status       VARCHAR(20)  NOT NULL,
    lat                DOUBLE       NOT NULL,
    lon                DOUBLE       NOT NULL,
    review_count       INT,
    average_rating     DOUBLE,
    business_hours     VARCHAR(100),
    break_time         VARCHAR(100),
    out_of_business_at DATETIME,
    created_at         DATETIME     NOT NULL,
    updated_at         DATETIME     NOT NULL,
    CONSTRAINT fk_store_member FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE reservation
(
    id                          BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id                    BIGINT   NOT NULL,
    member_id                   BIGINT   NOT NULL,
    reservation_date            DATE,
    reservation_time            VARCHAR(10),
    reservation_memo            VARCHAR(255),
    number_of_customer          INT,
    reservation_code            VARCHAR(4),
    reservation_visit_status    VARCHAR(50),
    reservation_approval_status VARCHAR(20),
    created_at                  DATETIME NOT NULL,
    updated_at                  DATETIME NOT NULL,
    CONSTRAINT fk_reservation_store FOREIGN KEY (store_id) REFERENCES store (id),
    CONSTRAINT fk_reservation_member FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE review
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id       BIGINT   NOT NULL,
    member_id      BIGINT   NOT NULL,
    reservation_id BIGINT   NOT NULL,
    message        VARCHAR(255),
    rating         FLOAT,
    review_status  VARCHAR(20),
    visited_date   DATE,
    created_at     DATETIME NOT NULL,
    updated_at     DATETIME NOT NULL,
    CONSTRAINT fk_review_store FOREIGN KEY (store_id) REFERENCES store (id),
    CONSTRAINT fk_review_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_review_reservation FOREIGN KEY (reservation_id) REFERENCES reservation (id)
);

CREATE TABLE refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    member_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member (id) 
);
```

### ERD

![image](https://github.com/jinyngg/store-project/assets/96164211/24bfcf3f-8ce8-4a0b-8288-b92642da7ad4)

## 1️⃣ 기능 구현 1
- [x] 공통 인증 구현
- [x] 회원가입(일반회원, 파트너 회원(점주)) , 파트너 회원 가입은 승인 조건은 없으며 가입 후 바로 이용 가능
- [x] 매장 점주(파트너 회원) 상점 등록(매장 명, 상점위치, 상점 설명 등..)

## 2️⃣ 기능 구현 2
- [x] 매장 검색, 상세 정보 확인(회원이 아니여도 가능)
- [x] 예약(일반회원 또는 파트너 회원)

## 3️⃣ 기능 구현 3
- [x] 예약 10분전에 도착하여 키오스크를 통해서 방문 확인
- [x] 예약 및 사용 이후에 리뷰
- [x] 점장은 승인/예약 거절

## 4️⃣ 추가 기능 구현
- [ ] 회원 정보 수정
- [ ] 회원 탈퇴
- [ ] 어드민 회원
- [x] 토큰 재발급
- [x] 매장 전체 보기
- [x] 점주가 관리하는 매장 조회
- [x] 예약 취소
- [x] 선택한 매장의 정보와 매장 예약 확인(파트너 회원)
- [ ] 리뷰 삭제 또는 리뷰 상태 변경

## 예외처리 

```Code, Message Status를 포함한 응답처리```

![image](https://github.com/jinyngg/store-project/assets/96164211/9a1983c4-4249-4d35-8b2d-132fa448eab2)

## 테스트

```Postman을 통한 테스트 진행```

![image](https://github.com/jinyngg/store-project/assets/96164211/72c27393-d21c-4c58-9c5f-478d515b7508)

## 생각해보기

필수 기능에 대해 구현과 같이 구현하면 좋을 추가 기능을 생각하며 작성했는데 생각보다 시간이 오래 걸렸다. 제출 이후 추가 작업!
그리고 아직 테스트 코드를 작성하는 것이 익숙하지 않아 포스트맨을 사용하여 테스트를 진행했다. 다음엔 테스트 코드를 작성하며 개발해봐야겠다.

## 피드백
