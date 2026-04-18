package com.v1.v1.controller;

import com.v1.v1.model.Product;
import com.v1.v1.model.Role;
import com.v1.v1.model.User;
import com.v1.v1.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Handles product CRUD for Warehouse Admin.
 * SOLID - OCP: New product types/views can be added without modifying this controller.
 */
@Controller
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String listProducts(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/add")
    public String addForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        model.addAttribute("product", new Product());
        return "admin/product-form";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult result, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        if (result.hasErrors()) return "admin/product-form";
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        productService.getProductById(id).ifPresent(p -> model.addAttribute("product", p));
        return "admin/product-form";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                 @Valid @ModelAttribute("product") Product product,
                                 BindingResult result, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        if (result.hasErrors()) return "admin/product-form";
        product.setId(id);
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }
}