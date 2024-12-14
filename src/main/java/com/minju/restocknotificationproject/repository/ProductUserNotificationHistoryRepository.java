package com.minju.restocknotificationproject.repository;

import com.minju.restocknotificationproject.entity.ProductNotificationHistory;
import com.minju.restocknotificationproject.entity.ProductUserNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductUserNotificationHistoryRepository extends JpaRepository<ProductUserNotificationHistory, Long> {
}
