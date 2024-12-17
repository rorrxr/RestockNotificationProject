package com.minju.restocknotificationproject.repository;

import com.minju.restocknotificationproject.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.userNotifications WHERE p.productId = :productId")
    Optional<Product> findByIdWithUserNotifications(@Param("productId") Long productId);

}
