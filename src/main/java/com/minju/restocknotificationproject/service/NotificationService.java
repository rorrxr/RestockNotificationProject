package com.minju.restocknotificationproject.service;

import com.google.common.util.concurrent.RateLimiter;
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

    // Repository 의존성 주입
    private final ProductRepository productRepository;
    private final ProductNotificationHistoryRepository notificationHistoryRepository;
    private final ProductUserNotificationRepository userNotificationRepository;
    private final ProductUserNotificationHistoryRepository userNotificationHistoryRepository;

    // 초당 500개의 요청을 허용하는 RateLimiter 설정
    private final RateLimiter rateLimiter = RateLimiter.create(500.0);

    /*
        재입고 알림을 발송하고 상태를 업데이트하는 메서드
        Long productId => 상품 ID
        return값은 ProductNotificationHistoryResponseDto 알림 기록에 대한 응답 DTO
     */
    @Transactional
    public ProductNotificationHistoryResponseDto sendRestockNotification(Long productId) {
        // 1. 상품 조회 및 검증
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("제품을 찾을 수 없습니다. ID=" + productId));

        // userNotifications(알림 설정된 유저 목록) 로드
        List<ProductUserNotification> users = product.getUserNotifications();

        // 2. 재입고 회차 업데이트
        product.setRestockRound(product.getRestockRound() + 1);
        productRepository.save(product);

        // 3. 재입고 알림 히스토리 생성 (IN_PROGRESS 상태로 초기화)
        ProductNotificationHistory notificationHistory = createNotificationHistory(product, product.getRestockRound());

        // 4. 유저별로 알림을 전송
        try {
            sendNotificationsToUsers(product, notificationHistory);
        } catch (Exception e) {
            // 알림 실패 시 상태를 ERROR로 업데이트
            updateNotificationStatus(notificationHistory, ProductNotificationHistory.NotificationStatus.CANCELED_BY_ERROR);
            throw e;
        }

        // 5. 모든 알림 전송이 성공하면 상태를 COMPLETED로 설정
        updateNotificationStatus(notificationHistory, ProductNotificationHistory.NotificationStatus.COMPLETED);

        // 6. 결과를 DTO로 반환
        return convertToResponseDto(notificationHistory);
    }

    /*
      재입고 알림 히스토리 생성
      Product product 상품 엔티티
      int restockRound 재입고 회차
      return값으로 ProductNotificationHistory 알림 히스토리 엔티티를 반환
     */
    private ProductNotificationHistory createNotificationHistory(Product product, int restockRound) {
        ProductNotificationHistory notificationHistory = new ProductNotificationHistory();
        notificationHistory.setProduct(product);
        notificationHistory.setRestockRound(restockRound);
        notificationHistory.setStatus(ProductNotificationHistory.NotificationStatus.IN_PROGRESS);
        return notificationHistoryRepository.save(notificationHistory);
    }

    /*
      활성화된 유저에게 알림을 전송
      Product product 상품 엔티티
      ProductNotificationHistory notificationHistory 알림 히스토리 엔티티
     */
    private void sendNotificationsToUsers(Product product, ProductNotificationHistory notificationHistory) {
        // 알림 설정이 활성화된 유저 필터링
        List<ProductUserNotification> users = product.getUserNotifications()
                .stream()
                .filter(ProductUserNotification::getIsActive)
                .filter(user -> user.getUserId() > notificationHistory.getLastNotifiedUserId()) // 마지막 전송된 유저 이후부터 전송
                .toList();

        // 각 유저에게 알림을 전송하며 RateLimiter 적용
        for (ProductUserNotification user : users) {
            // 4-1. 재고가 0 이하라면 알림 중단 및 상태 업데이트
            if (product.getStock() <= 0) {
                updateNotificationStatus(notificationHistory, ProductNotificationHistory.NotificationStatus.CANCELED_BY_SOLD_OUT);
                return;
            }

            // 4-2. Rate Limiter를 통해 속도 제한 적용
            rateLimiter.acquire();

            // 4-3. 유저 알림 기록 저장
            saveUserNotificationHistory(user, notificationHistory.getRestockRound());
            notificationHistory.setLastNotifiedUserId(user.getUserId()); // 마지막 알림 보낸 유저 ID 업데이트

            // 4-4. 재고 감소
            product.setStock(product.getStock() - 1);
            productRepository.save(product);
        }
    }

    /*
         유저 알림 기록 저장
         ProductUserNotification userNotification 유저 알림 설정
         int restockRound 재입고 회차
     */
    private void saveUserNotificationHistory(ProductUserNotification userNotification, int restockRound) {
        if (userNotification.getProduct() == null) {
            throw new IllegalStateException("제품을 찾지 못했습니다.");
        }

        ProductUserNotificationHistory userNotificationHistory = new ProductUserNotificationHistory();
        userNotificationHistory.setUserNotification(userNotification);
        userNotificationHistory.setProduct(userNotification.getProduct());
        userNotificationHistory.setUserId(userNotification.getUserId());
        userNotificationHistory.setRestockRound(restockRound);
        userNotificationHistory.setNotifiedAt(LocalDateTime.now());

        userNotificationHistoryRepository.save(userNotificationHistory);
    }

    /*
         알림 상태를 업데이트
         notificationHistory 알림 히스토리
         status 업데이트할 상태 값
     */
    private void updateNotificationStatus(ProductNotificationHistory notificationHistory, ProductNotificationHistory.NotificationStatus status) {
        notificationHistory.setStatus(status);
        notificationHistoryRepository.save(notificationHistory);
    }

    /*
         알림 히스토리 엔티티를 DTO로 변환
         notificationHistory 알림 히스토리
         ProductNotificationHistoryResponseDto 알림 기록에 대한 DTO를 반환
     */
    private ProductNotificationHistoryResponseDto convertToResponseDto(ProductNotificationHistory notificationHistory) {
        return new ProductNotificationHistoryResponseDto(
                notificationHistory.getProduct().getProductId(),
                notificationHistory.getRestockRound(),
                notificationHistory.getStatus().name(),
                notificationHistory.getLastNotifiedUserId()
        );
    }
}
