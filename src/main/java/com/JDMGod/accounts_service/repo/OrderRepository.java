package com.JDMGod.accounts_service.repo;

import com.JDMGod.accounts_service.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    List<Order> findByStatus(Order.OrderStatus status);

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status = :status")
    List<Order> findByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") Order.OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId")
    Long countOrdersByCustomerId(@Param("customerId") Long customerId);

    Optional<Order> findByPaymentIntentId(String paymentIntentId);
}
