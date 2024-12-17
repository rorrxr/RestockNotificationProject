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
    @Column(name = "id") // 상품 아이디(기본키)
    private Long productId;

    // 재입고 회차
    @Column(name = "restock_round")
    private Integer restockRound;

    // 재고 상태
    @Column(name = "stock")
    private Integer stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductUserNotification> userNotifications = new ArrayList<>();


}
