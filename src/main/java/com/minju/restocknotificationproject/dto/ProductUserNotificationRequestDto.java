package com.minju.restocknotificationproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUserNotificationRequestDto {
    private Long productId; // FK: 상품 아이디
    private Long userId;    // 유저 아이디
    private Boolean isActive;
}
