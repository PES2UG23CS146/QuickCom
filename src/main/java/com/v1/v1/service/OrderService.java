package com.v1.v1.service;

import com.v1.v1.model.*;
import com.v1.v1.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * GRASP - Creator:
 * OrderService creates Orders because it holds the initiating context:
 * the user, items, address. It is the right place to assemble the Order object.
 *
 * GRASP - Low Coupling:
 * OrderService delegates stock reduction to ProductService instead of
 * directly touching ProductRepository — keeps coupling low and responsibilities clear.
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    /**
     * Creates a new order from a cart (list of CartItems) for a user.
     * This is the CREATOR responsibility — Order is built here with full context.
     */
    public Order createOrder(User user, List<CartItem> items, String deliveryAddress) {
        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus(OrderStatus.PENDING);

        // Link each item to this order and reduce stock
        for (CartItem item : items) {
            item.setOrder(order);
            productService.reduceStock(item.getProduct().getId(), item.getQuantity());
        }

        order.setItems(items);
        order.computeTotal();
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public void cancelOrder(Long orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED) {
                order.setStatus(OrderStatus.CANCELLED);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
            } else {
                throw new RuntimeException("Order cannot be cancelled in current status: " + order.getStatus());
            }
        });
    }

    public void updateOrderStatus(Long orderId, OrderStatus status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        });
    }

    public void assignAgent(Long orderId, User agent) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setDeliveryAgent(agent);
            order.setStatus(OrderStatus.ASSIGNED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        });
    }

    public List<Order> getOrdersByAgent(User agent) {
        return orderRepository.findByDeliveryAgent(agent);
    }
}