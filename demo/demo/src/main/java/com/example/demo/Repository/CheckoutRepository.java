package com.example.demo.Repository;

import com.example.demo.Model.Checkout;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CheckoutRepository extends JpaRepository<Checkout, Long> {
    Checkout findByOrderCode(String orderCode);
    List<Checkout> findByUserId(Long userId);

    // Thêm các phương thức tìm kiếm theo trạng thái
    List<Checkout> findByOrderStatus(String orderStatus);
    List<Checkout> findByPaymentStatus(String paymentStatus);

    // Tìm đơn hàng trong khoảng thời gian
    List<Checkout> findByOrderDateBetween(Date startDate, Date endDate);

    List<Checkout> findByUser(User user);

    @Query("SELECT COUNT(c) FROM Checkout c WHERE c.orderStatus = :status")
    long countByOrderStatus(@Param("status") String status);

    @Query("SELECT SUM(c.orderTotal) FROM Checkout c WHERE (c.orderStatus = 'Đã giao hàng' OR c.paymentStatus = 'Đã thanh toán') AND c.orderDate > :sinceDate")
    Double sumRevenueSince(@Param("sinceDate") Date sinceDate);
}
