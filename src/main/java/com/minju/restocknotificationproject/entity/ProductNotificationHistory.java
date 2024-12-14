package com.minju.restocknotificationproject.entity;

import jakarta.persistence.*;
import lombok.Data;

// 상품별 재입고 알림 히스토리
@Data
@Entity
@Table(name = "product_notification_history")
public class ProductNotificationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품 아이디
    private Long productId;
    
    // 재입고 회차
    private Integer restockRound;

    // 재입고 알림 발송 상태
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    // 마지막 발송 유저 아이디
    private Long lastNotifiedUserId;

    // 재입고 알림 전송 상태
    public enum NotificationStatus {
        IN_PROGRESS,
        CANCELED_BY_SOLD_OUT,
        CANCELED_BY_ERROR,
        COMPLETED
    }
}