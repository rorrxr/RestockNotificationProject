package com.minju.restocknotificationproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 아이디

    // 재입고 회차
    private Integer restockRound;

    // 재고 상태
    private Integer stock;

    // 상품별 알림 히스토리 (1:N 관계)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductNotificationHistory> notificationHistories = new ArrayList<>();

    // 상품별 유저 알림 설정 (1:N 관계)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductUserNotification> userNotifications = new ArrayList<>();
}

