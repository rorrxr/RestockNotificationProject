package com.minju.restocknotificationproject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product_notification_history")
public class ProductNotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 재입고 회차
    @Column(name = "restock_round")
    private Integer restockRound;

    // 재입고 알림 발송 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NotificationStatus status;

    // 마지막 알림을 발송한 유저 아이디
    @Column(name = "last_notified_user_id")
    private Long lastNotifiedUserId;

    // 상태들을 관리하기 위한 enum
    public enum NotificationStatus {
        IN_PROGRESS,
        CANCELED_BY_SOLD_OUT,
        CANCELED_BY_ERROR,
        COMPLETED
    }
}
