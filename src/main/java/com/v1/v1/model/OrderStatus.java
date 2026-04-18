package com.v1.v1.model;

/**
 * SOLID - Open/Closed Principle:
 * New statuses can be added without changing any existing Order logic.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    ASSIGNED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    REFUNDED
}