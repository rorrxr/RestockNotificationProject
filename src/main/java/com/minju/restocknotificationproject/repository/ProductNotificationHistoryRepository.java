package com.minju.restocknotificationproject.repository;

import com.minju.restocknotificationproject.entity.ProductNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductNotificationHistoryRepository extends JpaRepository<ProductNotificationHistory, Long> {
}
