package com.minju.restocknotificationproject.service;

import com.minju.restocknotificationproject.dto.ProductNotificationHistoryResponseDto;
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
    public ProductNotificationHistoryResponseDto sendRestockNotification(Long productId) {
        // 1. 상품 조회 및 검증
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("제품을 찾을 수 없습니다. ID=" + productId));

        // Fetch join을 사용하여 userNotifications 로드
        List<ProductUserNotification> users = product.getUserNotifications();

        // 2. 재입고 회차 업데이트
        product.setRestockRound(product.getRestockRound() + 1);
        productRepository.save(product);

        // 3. 알림 히스토리 생성
        ProductNotificationHistory notificationHistory = createNotificationHistory(product, product.getRestockRound());

        // 4. 유저별 알림 발송
        try {
            sendNotificationsToUsers(product, notificationHistory);
        } catch (Exception e) {
            // 알림 실패 상태 업데이트
            updateNotificationStatus(notificationHistory, ProductNotificationHistory.NotificationStatus.CANCELED_BY_ERROR);
            throw e;
        }

        // 5. 알림 상태를 COMPLETED로 설정
        updateNotificationStatus(notificationHistory, ProductNotificationHistory.NotificationStatus.COMPLETED);

        // 6. DTO 변환 및 반환
        return convertToResponseDto(notificationHistory);
    }

    // 알림 히스토리 생성
    private ProductNotificationHistory createNotificationHistory(Product product, int restockRound) {
        ProductNotificationHistory notificationHistory = new ProductNotificationHistory();
        notificationHistory.setProduct(product); // 연관된 Product 객체 설정
        notificationHistory.setRestockRound(restockRound);
        notificationHistory.setStatus(ProductNotificationHistory.NotificationStatus.IN_PROGRESS);
        return notificationHistoryRepository.save(notificationHistory);
    }

    // 유저별 알림 발송
    private void sendNotificationsToUsers(Product product, ProductNotificationHistory notificationHistory) {
        List<ProductUserNotification> users = product.getUserNotifications()
                .stream()
                .filter(ProductUserNotification::getIsActive) // 활성화된 알림만 필터링
                .toList();

        for (ProductUserNotification user : users) {
            if (product.getStock() <= 0) {
                updateNotificationStatus(notificationHistory, ProductNotificationHistory.NotificationStatus.CANCELED_BY_SOLD_OUT);
                return;
            }

            saveUserNotificationHistory(user, notificationHistory.getRestockRound());
            notificationHistory.setLastNotifiedUserId(user.getUserId());

            // 재고 감소
            product.setStock(product.getStock() - 1);
            productRepository.save(product);
        }
    }

    // 유저 알림 기록 저장
    private void saveUserNotificationHistory(ProductUserNotification userNotification, int restockRound) {
        if (userNotification.getProduct() == null) {
            throw new IllegalStateException("제품을 찾지 못했습니다.");
        }

        ProductUserNotificationHistory userNotificationHistory = new ProductUserNotificationHistory();
        userNotificationHistory.setUserNotification(userNotification);
        userNotificationHistory.setProduct(userNotification.getProduct()); // 연관된 Product 설정
        userNotificationHistory.setUserId(userNotification.getUserId());
        userNotificationHistory.setRestockRound(restockRound);
        userNotificationHistory.setNotifiedAt(LocalDateTime.now());

        // 저장
        userNotificationHistoryRepository.save(userNotificationHistory);
    }



    // 알림 상태 업데이트
    private void updateNotificationStatus(ProductNotificationHistory notificationHistory, ProductNotificationHistory.NotificationStatus status) {
        notificationHistory.setStatus(status);
        notificationHistoryRepository.save(notificationHistory);
    }

    // DTO 변환
    private ProductNotificationHistoryResponseDto convertToResponseDto(ProductNotificationHistory notificationHistory) {
        if (notificationHistory == null) {
            throw new IllegalArgumentException("알림이 없습니다.");
        }

        return new ProductNotificationHistoryResponseDto(
                notificationHistory.getProduct().getProductId(), // Product에서 productId 가져오기
                notificationHistory.getRestockRound(),
                notificationHistory.getStatus().name(), // ENUM을 String으로 변환
                notificationHistory.getLastNotifiedUserId()
        );
    }
}
