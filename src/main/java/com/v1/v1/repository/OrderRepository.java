package com.v1.v1.repository;

import com.v1.v1.model.Order;
import com.v1.v1.model.OrderStatus;
import com.v1.v1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByDeliveryAgent(User agent);
    List<Order> findByUserOrderByCreatedAtDesc(User user);
}