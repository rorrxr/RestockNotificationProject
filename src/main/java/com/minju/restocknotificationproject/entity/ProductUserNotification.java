package com.minju.restocknotificationproject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_user_notification")
public class ProductUserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // 인덱스 (기본키)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // FK 컬럼
    private Product product;

    // 유저 아이디
    @Column(name = "user_id")
    private Long userId;

    // 활성화 여부
    @Column(name = "is_active")
    private Boolean isActive;

    // 생성 날짜
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 수정 날짜
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
