package com.minju.restocknotificationproject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_user_notification_history")
public class ProductUserNotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // 인덱스 (기본키)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // 상품 아이디 (외래키)
    private Product product;

    // 알림 설정 정보와 유저 정보가 필요하여 userNotification을 조인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_notification_id", nullable = false)
    private ProductUserNotification userNotification;

    // 유저 아이디
    @Column(name = "user_id")
    private Long userId;

    // 재입고 회차
    @Column(name = "restock_round")
    private Integer restockRound;

    // 발송 날짜
    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;
}
