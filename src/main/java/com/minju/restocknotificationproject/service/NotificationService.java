package com.minju.restocknotificationproject.service;

import com.minju.restocknotificationproject.entity.Product;
import com.minju.restocknotificationproject.entity.ProductNotificationHistory;
import com.minju.restocknotificationproject.entity.ProductUserNotification;
import com.minju.restocknotificationproject.entity.ProductUserNotificationHistory;
import com.minju.restocknotificationproject.repository.ProductNotificationHistoryRepository;
import com.minju.restocknotificationproject.repository.ProductRepository;
import com.minju.restocknotificationproject.repository.ProductUserNotificationHistoryRepository;
import com.minju.restocknotificationproject.repository.ProductUserNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final ProductRepository productRepository;
    private final ProductNotificationHistoryRepository notificationHistoryRepository;
    private final ProductUserNotificationRepository userNotificationRepository;
    private final ProductUserNotificationHistoryRepository userNotificationHistoryRepository;

    @Transactional
    public void sendRestockNotification(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("제품을 찾을 수 없습니다."));

        product.setRestockRound(product.getRestockRound() + 1);
        productRepository.save(product);

        ProductNotificationHistory notificationHistory = new ProductNotificationHistory();
        notificationHistory.setProductId(productId);
        notificationHistory.setRestockRound(product.getRestockRound());
        notificationHistory.setStatus(ProductNotificationHistory.NotificationStatus.IN_PROGRESS);
        notificationHistoryRepository.save(notificationHistory);

        List<ProductUserNotification> users = userNotificationRepository.findByProductIdAndIsActiveTrueOrderByIdAsc(productId);

        for (ProductUserNotification user : users) {
            if (product.getStock() <= 0) {
                notificationHistory.setStatus(ProductNotificationHistory.NotificationStatus.CANCELED_BY_SOLD_OUT);
                notificationHistoryRepository.save(notificationHistory);
                return;
            }

            ProductUserNotificationHistory userNotificationHistory = new ProductUserNotificationHistory();
            userNotificationHistory.setProductId(productId);
            userNotificationHistory.setUserId(user.getUserId());
            userNotificationHistory.setRestockRound(product.getRestockRound());
            userNotificationHistory.setNotifiedAt(LocalDateTime.now());
            userNotificationHistoryRepository.save(userNotificationHistory);

            notificationHistory.setLastNotifiedUserId(user.getUserId());

            product.setStock(product.getStock() - 1);
            productRepository.save(product);
        }

        notificationHistory.setStatus(ProductNotificationHistory.NotificationStatus.COMPLETED);
        notificationHistoryRepository.save(notificationHistory);
    }
}
