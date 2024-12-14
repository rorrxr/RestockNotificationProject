package com.minju.restocknotificationproject.controller;

import com.minju.restocknotificationproject.service.NotificationService;
import lombok.RequiredArgsConstructor;
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
    public void sendRestockNotification(@PathVariable Long productId) {
        notificationService.sendRestockNotification(productId);
    }

    @PostMapping("/admin/{productId}/notifications/re-stock")
    public void resendRestockNotification(@PathVariable Long productId) {
        notificationService.sendRestockNotification(productId);
    }
}
