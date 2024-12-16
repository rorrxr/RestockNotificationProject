package com.minju.restocknotificationproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ProductUserNotificationResponseDto {
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
