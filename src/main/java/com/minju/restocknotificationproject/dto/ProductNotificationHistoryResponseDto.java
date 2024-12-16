package com.minju.restocknotificationproject.dto;

import com.minju.restocknotificationproject.entity.ProductNotificationHistory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductNotificationHistoryResponseDto {
    private Long id;

    // 상품 아이디
    private Long productId;

    // 재입고 회차
    private Integer restockRound;

    // 재입고 알림 발송 상태
    @Enumerated(EnumType.STRING)
    private ProductNotificationHistory.NotificationStatus status;

    // 마지막 발송 유저 아이디
    private Long lastNotifiedUserId;

    // 재입고 알림 전송 상태
    public enum NotificationStatus {
        IN_PROGRESS,
        CANCELED_BY_SOLD_OUT,
        CANCELED_BY_ERROR,
        COMPLETED
    }
}
