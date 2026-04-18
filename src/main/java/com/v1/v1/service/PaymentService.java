package com.v1.v1.service;

import com.v1.v1.model.*;
import com.v1.v1.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * GRASP - Indirection:
 * PaymentService is the intermediary layer between controllers and payment data.
 * Controllers never access PaymentRepository directly — all go through here.
 * This shields the system if payment provider changes.
 *
 * GRASP - Polymorphism:
 * processPayment handles different PaymentMethod types uniformly via the same method.
 * Future methods (UPI, crypto) can be added without changing callers.
 *
 * SOLID - Liskov Substitution:
 * Any PaymentMethod value substitutes correctly in processPayment
 * without changing the method's behavior contract.
 */
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    /**
     * Processes payment for a given order.
     * GRASP Polymorphism: regardless of method (UPI/card/COD), payment is processed uniformly.
     */
    public Payment processPayment(Order order, PaymentMethod method) {
        // Check if payment already exists
        Optional<Payment> existing = paymentRepository.findByOrderId(order.getId());
        if (existing.isPresent()) {
            throw new RuntimeException("Payment already exists for this order.");
        }

        Payment payment = new Payment(order, order.getTotalAmount(), method);

        // Simulate payment gateway — COD is always pending, others succeed immediately
        if (method == PaymentMethod.CASH_ON_DELIVERY) {
            payment.setStatus(PaymentStatus.PENDING);
        } else {
            payment.setStatus(PaymentStatus.COMPLETED);
            // Mark order as confirmed once paid
            orderService.updateOrderStatus(order.getId(), OrderStatus.CONFIRMED);
        }

        return paymentRepository.save(payment);
    }

    public Optional<Payment> getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    /**
     * Authorizes a refund for a payment.
     * Admin-only operation.
     */
    public void authorizeRefund(Long paymentId) {
        paymentRepository.findById(paymentId).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            // Update order status to refunded
            orderService.updateOrderStatus(payment.getOrder().getId(), OrderStatus.REFUNDED);
        });
    }
}