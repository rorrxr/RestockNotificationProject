package com.minju.restocknotificationproject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// 상품별 재입고 알림을 설정한 유저
@Data
@Entity
@Table(name = "product_user_notification")
public class ProductUserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품 아이디
    private Long productId;
    
    // 유저 아이디
    private Long userId;
    
    // 활성화 여부
    private Boolean isActive;
    
    // 생성 날짜
    private LocalDateTime createdAt;
    
    // 수정 날짜
    private LocalDateTime updatedAt;
}
