package com.v1.v1.service;

import com.v1.v1.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * GRASP - Pure Fabrication:
 * RoleActionService does not map to any real-world domain object.
 * It exists purely to group role-specific cross-cutting actions cleanly,
 * improving cohesion without polluting domain models with unrelated logic.
 *
 * GRASP - Controller (GRASP pattern):
 * Acts as the system-event handler for role-specific operations.
 * Centralizes "what can each role do" in one service — a system controller.
 *
 * SOLID - Interface Segregation Principle:
 * Clients only use the methods relevant to their role.
 * Admin doesn't call agent methods, agent doesn't call admin methods.
 */
@Service
public class RoleActionService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ProductService productService;

    // ─── ADMIN specific actions ───────────────────────────────────────────────

    /**
     * Admin: Get full monitoring dashboard data.
     * GRASP Pure Fabrication: This method aggregates data for the admin view
     * without any single domain object being responsible for it.
     */
    public AdminDashboardData getAdminDashboardData() {
        AdminDashboardData data = new AdminDashboardData();
        data.setAllOrders(orderService.getAllOrders());
        data.setPendingOrders(orderService.getOrdersByStatus(OrderStatus.PENDING));
        data.setAllPayments(paymentService.getAllPayments());
        data.setAllFeedback(feedbackService.getAllFeedback());
        data.setAllProducts(productService.getAllProducts());
        return data;
    }

    /**
     * Admin: Assign delivery agent to order.
     */
    public void adminAssignAgent(Long orderId, User agent) {
        orderService.assignAgent(orderId, agent);
    }

    /**
     * Admin: Authorize a refund.
     */
    public void adminAuthorizeRefund(Long paymentId) {
        paymentService.authorizeRefund(paymentId);
    }

    // ─── AGENT specific actions ───────────────────────────────────────────────

    /**
     * Agent: Get all orders assigned to this agent.
     */
    public List<Order> getAgentOrders(User agent) {
        return orderService.getOrdersByAgent(agent);
    }

    /**
     * Agent: Mark order as out for delivery.
     */
    public void agentMarkOutForDelivery(Long orderId) {
        orderService.updateOrderStatus(orderId, OrderStatus.OUT_FOR_DELIVERY);
    }

    /**
     * Agent: Mark order as delivered.
     */
    public void agentMarkDelivered(Long orderId) {
        orderService.updateOrderStatus(orderId, OrderStatus.DELIVERED);
    }

    // ─── USER specific actions ────────────────────────────────────────────────

    /**
     * User: Get their order history.
     */
    public List<Order> getUserOrders(User user) {
        return orderService.getOrdersByUser(user);
    }

    /**
     * User: Cancel their own order.
     */
    public void userCancelOrder(Long orderId) {
        orderService.cancelOrder(orderId);
    }

    /**
     * User: Submit feedback for a delivered order.
     */
    public void userSubmitFeedback(Order order, User user, int rating, String comment) {
        feedbackService.submitFeedback(order, user, rating, comment);
    }
}