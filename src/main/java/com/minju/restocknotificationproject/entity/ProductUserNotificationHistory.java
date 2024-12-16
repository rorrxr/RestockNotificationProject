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
    private Long id; // 상품 아이디

    // 상품 알림 설정과 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_notification_id", nullable = false)
    private ProductUserNotification userNotification;

    // 재입고 회차
    private Integer restockRound;

    // 발송 날짜
    private LocalDateTime notifiedAt;
}
