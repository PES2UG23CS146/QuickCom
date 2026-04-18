package com.v1.v1.service;

import com.v1.v1.model.Product;
import com.v1.v1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * SOLID - Open/Closed Principle:
 * ProductService can be extended (e.g., DiscountedProductService) without
 * modifying this base service. Core CRUD is stable and closed to modification.
 *
 * GRASP - Low Coupling:
 * ProductService only depends on ProductRepository — no tight coupling to
 * Order or User services. Changes in those don't ripple here.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllAvailableProducts() {
        return productRepository.findByAvailableTrue();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    // Reduce stock after order
    public void reduceStock(Long productId, int quantity) {
        productRepository.findById(productId).ifPresent(p -> {
            if (p.getStockQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock for: " + p.getName());
            }
            p.setStockQuantity(p.getStockQuantity() - quantity);
            productRepository.save(p);
        });
    }
}