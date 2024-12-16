package com.minju.restocknotificationproject.controller;

import com.minju.restocknotificationproject.dto.ProductNotificationHistoryResponseDto;
import com.minju.restocknotificationproject.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/{productId}/notifications/re-stock")
    public ResponseEntity<ProductNotificationHistoryResponseDto> sendRestockNotification(@PathVariable Long productId) {
        ProductNotificationHistoryResponseDto response = notificationService.sendRestockNotification(productId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/{productId}/notifications/re-stock")
    public ResponseEntity<ProductNotificationHistoryResponseDto> resendRestockNotification(@PathVariable Long productId) {
        ProductNotificationHistoryResponseDto response = notificationService.sendRestockNotification(productId);
        return ResponseEntity.ok(response);

    }
}
