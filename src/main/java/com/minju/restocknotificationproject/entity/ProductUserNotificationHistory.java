package com.minju.restocknotificationproject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// 상품 + 유저별 알림 히스토리
@Data
@Entity
@Table(name = "product_user_notification_history")
public class ProductUserNotificationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품 아이디
    private Long productId;
    
    // 유저 아이디
    private Long userId;
    
    // 재입고 회차
    private Integer restockRound;
    
    // 발송 날짜
    private LocalDateTime notifiedAt;
}
