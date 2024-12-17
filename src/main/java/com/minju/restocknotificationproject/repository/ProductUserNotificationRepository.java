package com.minju.restocknotificationproject.repository;

import com.minju.restocknotificationproject.entity.ProductUserNotification;
import com.minju.restocknotificationproject.entity.ProductUserNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductUserNotificationRepository extends JpaRepository<ProductUserNotification, Long> {
}
