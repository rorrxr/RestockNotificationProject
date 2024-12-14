package com.minju.restocknotificationproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 재입고 회차
    private Integer restockRound;
    
    // 재고 상태
    private Integer stock;
}