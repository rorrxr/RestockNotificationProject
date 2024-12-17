package com.minju.restocknotificationproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductNotificationHistoryResponseDto {
    private Long productId; // FK: 상품 아이디
    private Integer restockRound;
    private String status; // 상태 ENUM을 String으로 전달
    private Long lastNotifiedUserId;
}
