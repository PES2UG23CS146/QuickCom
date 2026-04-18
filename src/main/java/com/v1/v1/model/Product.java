package com.v1.v1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Product entity — represents items available in the warehouse.
 * SOLID - Open/Closed Principle:
 * Product is open for extension (e.g., subclasses for perishable/electronics)
 * but closed for modification — core fields are stable.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name required")
    @Column(nullable = false)
    private String name;

    private String description;

    @Min(value = 0, message = "Price must be positive")
    @Column(nullable = false)
    private double price;

    @Min(value = 0, message = "Stock cannot be negative")
    @Column(nullable = false)
    private int stockQuantity;

    private String category;

    @Column(nullable = false)
    private boolean available = true;

    // Constructors
    public Product() {}

    public Product(String name, String description, double price, int stockQuantity, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}