package com.minju.restocknotificationproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "product_user_notification")
public class ProductUserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId; // 상품 아이디

    // 상품과 연관 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 유저 아이디
    private Long userId;

    // 활성화 여부
    private Boolean isActive;

    // 생성 날짜
    private LocalDateTime createdAt;

    // 수정 날짜
    private LocalDateTime updatedAt;

    // 유저 알림 히스토리 (1:N 관계)
    @OneToMany(mappedBy = "userNotification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductUserNotificationHistory> notificationHistories = new ArrayList<>();
}
