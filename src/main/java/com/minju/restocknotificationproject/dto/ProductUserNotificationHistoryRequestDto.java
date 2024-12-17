package com.minju.restocknotificationproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUserNotificationHistoryRequestDto {
    private Long productId; // 상품 아이디
    private Long userId;    // 유저 아이디
    private Integer restockRound;
    private LocalDateTime notifiedAt;
}
