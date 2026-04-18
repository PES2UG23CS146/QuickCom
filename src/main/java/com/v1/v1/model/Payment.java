package com.v1.v1.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Payment entity — tracks payment per order.
 *
 * GRASP - Indirection:
 * Payment acts as an intermediary between Order and financial records,
 * decoupling the Order from direct payment processing details.
 *
 * SOLID - Liskov Substitution Principle:
 * PaymentStatus is open for extension. Any subtype of PaymentStatus behavior
 * (e.g., adding PARTIAL_REFUND) substitutes correctly without breaking callers.
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private String transactionId;

    public Payment() {}

    public Payment(Order order, double amount, PaymentMethod method) {
        this.order = order;
        this.amount = amount;
        this.method = method;
        this.transactionId = "TXN-" + System.currentTimeMillis();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}