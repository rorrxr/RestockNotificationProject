package com.minju.restocknotificationproject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product_notification_history")
public class ProductNotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId; // Primary Key

    // 상품과의 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // Foreign Key 매핑
    private Product product;

    // 재입고 회차
    private Integer restockRound;

    // 재입고 알림 발송 상태
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    // 마지막 발송 유저 아이디
    private Long lastNotifiedUserId;

    public enum NotificationStatus {
        IN_PROGRESS,
        CANCELED_BY_SOLD_OUT,
        CANCELED_BY_ERROR,
        COMPLETED
    }
}

