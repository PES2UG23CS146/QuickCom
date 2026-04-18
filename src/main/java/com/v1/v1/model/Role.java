package com.v1.v1.model;

/**
 * GRASP - Protected Variations:
 * Encapsulating roles as an enum shields other parts of the system
 * from changes. Adding a new role here doesn't break existing logic.
 */
public enum Role {
    USER,
    WAREHOUSE_ADMIN,
    DELIVERY_AGENT
}