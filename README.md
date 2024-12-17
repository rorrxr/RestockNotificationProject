# 개인 기업과제 프로젝트 2번 : 재입고 알림 시스템
- 개발환경 : SpringBoot, Spring Data JPA, MySql, Docker, DBeaver
- 팀 스터디 8조 : 김민주, 정닛시, 양한서

## 과제 설명

- 상품이 재입고 되었을 때, 재입고 알림을 설정한 유저들에게 재입고 알림을 보내줍니다.
__ __ __ __ __ __ __

## **비즈니스 요구 사항**

- 재입고 알림을 전송하기 전, 상품의 재입고 회차를 1 증가 시킨다.
    - 실제 서비스에서는 다른 형태로 관리하지만, 과제에서는 직접 관리한다.
- 상품이 재입고 되었을 때, 재입고 알림을 설정한 유저들에게 알림 메시지를 전달해야 한다.
    - ProductUserNotification 테이블에 존재하는 유저는 모두 재입고 알림을 설정했다고 가정한다.
- 재입고 알림은 재입고 알림을 설정한 유저 순서대로 메시지를 전송한다.
- 회차별 재입고 알림을 받은 유저 목록을 저장해야 한다.
- 재입고 알림을 보내던 중 재고가 모두 없어진다면 알림 보내는 것을 중단합니다.
- 재입고 알림 전송의 상태를 DB 에 저장해야 한다.
    - IN_PROGRESS (발송 중)
    - CANCELED_BY_SOLD_OUT (품절에 의한 발송 중단)
    - CANCELED_BY_ERROR (예외에 의한 발송 중단)
        - 서드 파티 연동에서의 예외 를 의미한다.
    - COMPLETED (완료)
__ __ __ __ __ __ __

## 기술적 요구 사항

- 알림 메시지는 1초에 최대 500개의 요청을 보낼 수 있다.
    - 서드 파티 연동을 하진 않고, ProductNotificationHistory 테이블에 데이터를 저장한다.
- Mysql 조회 시, 인덱스를 잘 탈 수 있게 설계해야 합니다.
- 설계해야 할 테이블 목록
    1. Product (상품)
        1. 상품 아이디
        2. 재입고 회차
        3. 재고 상태
    2. ProductNotificationHistory (상품별 재입고 알림 히스토리)
        1. 상품 아이디
        2. 재입고 회차
        3. 재입고 알림 발송 상태
        4. 마지막 발송 유저 아이디
    3. ProductUserNotification (상품별 재입고 알림을 설정한 유저)
        1. 상품 아이디
        2. 유저 아이디
        3. 활성화 여부
        4. 생성 날짜
        5. 수정 날짜
    4. ProductUserNotificationHistory (상품 + 유저별 알림 히스토리)
        1. 상품 아이디 
        2. 유저 아이디 
        3. 재입고 회차 
        4. 발송 날짜
- (Optional) 예외에 의해 알림 메시지 발송이 실패한 경우, manual 하게 상품 재입고 알림 메시지를 다시 보내는 API를 호출한다면 마지막으로 전송 성공한 이후 유저부터 다시 알림 메시지를 보낼 수 있어야 한다.
    - 10번째 유저까지 알림 메시지 전송에 성공했다면, 다음 요청에서 11번째 유저부터 알림 메시지를 전송할 수 있어야 한다.
- 시스템 구조 상 비동기로 처리 되어야 하는 부분은 존재하지 않는다고 가정합니다.
- (Optional) 테스트 코드를 작성하면 좋습니다.
__ __ __ __ __ __ __

## 고려하지 않아도 되는 사항

- 회원 가입, 로그인은 고려하지 않습니다.
__ __ __ __ __ __ __

## API 스펙

### 재입고 알림 전송 API

- POST  /products/{productId}/notifications/re-stock

### 재입고 알림 전송 API (manual)

- POST /admin/products/{productId}/notifications/re-stock

**RequestBody**

NONE.

__ __ __ __ __ __ __

## docker 환경 설정

Dockerfile

```
FROM openjdk:17-jdk
WORKDIR /app

COPY build/libs/RestockNotificationProject-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

docker-compose.yml

```
services:
  database:
    container_name: mysql_restock
    image: mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: ****
      MYSQL_DATABASE: restock
    ports:
      - "3306:3306"
    volumes:
      - ./mysql/conf.d:/etc/mysql/conf.d # MySQL 설정 파일 위치
    command:
      - "mysqld"
      - "--character-set-server=utf8mb4"
      - "--collation-server=utf8mb4_general_ci"
    networks:
      - test_network

  application:
    container_name: docker-compose-restock
    restart: on-failure
    build:
      context: ./
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql_restock:3306/restock?useSSL=false&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: "****"
      SPRING_DATASOURCE_PASSWORD: "****"
    depends_on:
      - database
    networks:
      - test_network

networks:
  test_network:
```

__ __ __ __ __ __ __

## 트러블 슈팅 사례

## 1. Foreign Key 설정 오류

### 문제
- `product_user_notification_history` 테이블에서 `product_id`에 설정된 **Foreign Key Constraint**가 위배되어 오류가 발생했습니다.
- `SQL Error [1452] [23000]: Cannot add or update a child row: a foreign key constraint fails`라는 오류 메시지가 나타났습니다.

### 원인
- `product_user_notification_history` 테이블에 데이터를 삽입할 때 `product_id` 값이 **`product` 테이블에 존재하지 않았습니다.**.
- `ProductUserNotificationHistory` 엔티티의 `product` 필드에 `null`이 들어가거나, 영속 상태의 엔티티가 아닌 값이 설정되었습니다.
- 서비스 코드에서 연관 관계를 제대로 설정하지 않아 `product_id`가 누락되었습니다.

---

### 해결
#### **1. Product 엔티티 필드명과 컬럼명 정리**
`Product` 엔티티의 `productId` 필드명을 `id`로 변경해 데이터베이스의 `id` 컬럼과 일치시켰습니다.

**변경된 Product 엔티티**:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id") // 테이블 컬럼과 일치
private Long id;
```

#### **2. Service 코드 수정**
서비스 코드에서 `ProductUserNotificationHistory` 엔티티를 저장할 때 `product` 필드를 `userNotification.getProduct()`로 설정하고, 값이 **null인지 확인**하여 처리했습니다.

**수정된 메서드**:
```java
private void saveUserNotificationHistory(ProductUserNotification userNotification, int restockRound) {
    if (userNotification.getProduct() == null) {
        throw new IllegalStateException("Product cannot be null for user notification history.");
    }

    ProductUserNotificationHistory userNotificationHistory = new ProductUserNotificationHistory();
    userNotificationHistory.setUserNotification(userNotification); // 연관 관계 설정
    userNotificationHistory.setProduct(userNotification.getProduct()); // Product 설정
    userNotificationHistory.setUserId(userNotification.getUserId());
    userNotificationHistory.setRestockRound(restockRound);
    userNotificationHistory.setNotifiedAt(LocalDateTime.now());

    userNotificationHistoryRepository.save(userNotificationHistory);
}
```

#### **3. 데이터베이스 초기 데이터 삽입 순서 확인**
`product` 테이블에 `id` 값이 존재하는지 확인 후, `product_user_notification`과 `product_user_notification_history` 테이블에 데이터를 삽입했습니다.

**데이터 삽입 SQL**:
```sql
-- 1. product 테이블 데이터 삽입
INSERT INTO product (id, restock_round, stock) VALUES (1, 0, 50), (2, 0, 20);

-- 2. product_user_notification 테이블 데이터 삽입
INSERT INTO product_user_notification (id, product_id, user_id, is_active, created_at, updated_at)
VALUES (1, 1, 101, TRUE, NOW(), NOW()), (2, 1, 102, TRUE, NOW(), NOW());

-- 3. product_user_notification_history 테이블 데이터 삽입
INSERT INTO product_user_notification_history (id, user_notification_id, product_id, restock_round, notified_at)
VALUES (1, 1, 1, 0, '2024-12-01 10:00:00');
```

---

### 결과
- `ProductUserNotificationHistory` 테이블의 `product_id`가 `product` 테이블과 정확히 매핑되어 데이터가 정상적으로 삽입되었습니다.
- 서비스 코드에서 연관 관계를 명확하게 설정해 데이터 무결성을 유지했습니다.

---

### 느낀점
- **Foreign Key 제약 조건**은 데이터 무결성을 보장하지만, 삽입 순서나 연관 관계 설정이 잘못되면 오류가 발생할 수 있습니다.
- 엔티티와 테이블 구조를 정확히 이해하고 **서비스 코드에서 영속 상태의 엔티티를 사용해야 한다는 점**을 배웠습니다.
- 데이터 삽입 시 **외래 키 참조 테이블에 먼저 데이터를 삽입**해야 한다는 원칙을 다시 확인했습니다.

---

### 참고
- **연관 관계 설정**: `@ManyToOne`과 `@JoinColumn` 어노테이션 사용합니다.
- **오류 로그 분석**: `SQL Error [1452]`는 대부분 참조하는 키가 존재하지 않을 때 발생합니다.
- **데이터 삽입 순서**: 테이블 간 관계를 고려하여 데이터 삽입해야 합니다.

---

### 코드 개선 후 검증 결과
**Postman 테스트**를 통해 `/products/{id}/notifications/re-stock` 엔드포인트가 정상 동작하는 것을 확인했습니다.  
데이터베이스에 연관 데이터가 올바르게 삽입된 것을 MySQL 쿼리로 검증했습니다.