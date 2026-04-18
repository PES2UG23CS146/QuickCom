package com.v1.v1.service;

import com.v1.v1.model.Feedback;
import com.v1.v1.model.Order;
import com.v1.v1.model.Payment;
import com.v1.v1.model.Product;

import java.util.List;

/**
 * GRASP - Pure Fabrication:
 * AdminDashboardData is a fabricated value object used to transfer
 * aggregated dashboard information cleanly. No domain model owns this data naturally.
 *
 * SOLID - Interface Segregation Principle:
 * Only exposes fields that the Admin dashboard needs.
 * Agent and User dashboards have separate data holders — not this one.
 */
public class AdminDashboardData {

    private List<Order> allOrders;
    private List<Order> pendingOrders;
    private List<Payment> allPayments;
    private List<Feedback> allFeedback;
    private List<Product> allProducts;

    public List<Order> getAllOrders() { return allOrders; }
    public void setAllOrders(List<Order> allOrders) { this.allOrders = allOrders; }

    public List<Order> getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(List<Order> pendingOrders) { this.pendingOrders = pendingOrders; }

    public List<Payment> getAllPayments() { return allPayments; }
    public void setAllPayments(List<Payment> allPayments) { this.allPayments = allPayments; }

    public List<Feedback> getAllFeedback() { return allFeedback; }
    public void setAllFeedback(List<Feedback> allFeedback) { this.allFeedback = allFeedback; }

    public List<Product> getAllProducts() { return allProducts; }
    public void setAllProducts(List<Product> allProducts) { this.allProducts = allProducts; }

    // Computed stats
    public int getTotalOrders() { return allOrders != null ? allOrders.size() : 0; }
    public int getPendingCount() { return pendingOrders != null ? pendingOrders.size() : 0; }
    public double getTotalRevenue() {
        if (allPayments == null) return 0;
        return allPayments.stream().mapToDouble(Payment::getAmount).sum();
    }
    public double getAverageRating() {
        if (allFeedback == null || allFeedback.isEmpty()) return 0;
        return allFeedback.stream().mapToInt(Feedback::getRating).average().orElse(0);
    }
}