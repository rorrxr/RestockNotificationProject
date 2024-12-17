package com.minju.restocknotificationproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private Long productId;

    // 재입고 회차
    private Integer restockRound;

    // 재고 상태
    private Integer stock;
}
