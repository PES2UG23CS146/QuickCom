package com.v1.v1.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity — aggregates CartItems and links to User and DeliveryAgent.
 *
 * GRASP - Creator:
 * Order is the natural creator of CartItems because it contains and records them.
 * The Order "owns" its line items.
 *
 * GRASP - Low Coupling:
 * Order references User and DeliveryAgent only by ID association (JoinColumn),
 * not by embedding full objects unnecessarily. This reduces direct dependency chains.
 *
 * SOLID - Open/Closed Principle:
 * OrderStatus enum lets us extend statuses without modifying Order class logic.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Low Coupling: DeliveryAgent linked optionally — not mandatory at order creation
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User deliveryAgent;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CartItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private double totalAmount;

    private String deliveryAddress;

    // Recompute total from items
    public void computeTotal() {
        this.totalAmount = items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public Order() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public User getDeliveryAgent() { return deliveryAgent; }
    public void setDeliveryAgent(User deliveryAgent) { this.deliveryAgent = deliveryAgent; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
}