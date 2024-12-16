package com.minju.restocknotificationproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUserNotificationHistoryResponseDto {
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
