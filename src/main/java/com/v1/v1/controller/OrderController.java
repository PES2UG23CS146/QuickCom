package com.v1.v1.controller;

import com.v1.v1.model.*;
import com.v1.v1.service.OrderService;
import com.v1.v1.service.ProductService;
import com.v1.v1.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles order placement and tracking for Users.
 * GRASP - Creator: OrderController initiates order creation with user + cart context.
 * GRASP - Low Coupling: Delegates all logic to OrderService and ProductService.
 */
@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    // ─── Browse Products & Add to Cart ───────────────────────────────────────

    @GetMapping("/user/shop")
    public String shop(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.USER) return "redirect:/login";
        model.addAttribute("products", productService.getAllAvailableProducts());
        Map<Long, Integer> cart = getCart(session);
        model.addAttribute("cart", cart);
        return "user/shop";
    }

    @PostMapping("/user/cart/add")
    public String addToCart(@RequestParam Long productId,
                             @RequestParam(defaultValue = "1") int quantity,
                             HttpSession session) {
        Map<Long, Integer> cart = getCart(session);
        cart.merge(productId, quantity, Integer::sum);
        session.setAttribute("cart", cart);
        return "redirect:/user/shop";
    }

    @GetMapping("/user/cart")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.USER) return "redirect:/login";
        Map<Long, Integer> cart = getCart(session);
        List<CartItem> items = buildCartItems(cart);
        double total = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        model.addAttribute("cartItems", items);
        model.addAttribute("total", total);
        return "user/cart";
    }

    @PostMapping("/user/cart/remove")
    public String removeFromCart(@RequestParam Long productId, HttpSession session) {
        Map<Long, Integer> cart = getCart(session);
        cart.remove(productId);
        session.setAttribute("cart", cart);
        return "redirect:/user/cart";
    }

    @PostMapping("/user/order/place")
    public String placeOrder(@RequestParam String deliveryAddress, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.USER) return "redirect:/login";
        Map<Long, Integer> cart = getCart(session);
        if (cart.isEmpty()) {
            model.addAttribute("error", "Your cart is empty.");
            return "redirect:/user/cart";
        }
        try {
            List<CartItem> items = buildCartItems(cart);
            Order order = orderService.createOrder(user, items, deliveryAddress);
            session.removeAttribute("cart");
            return "redirect:/user/orders?placed=" + order.getId();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/user/cart";
        }
    }

    @GetMapping("/user/orders")
    public String myOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.USER) return "redirect:/login";
        model.addAttribute("orders", orderService.getOrdersByUser(user));
        return "user/orders";
    }

    @GetMapping("/user/orders/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";
        try {
            orderService.cancelOrder(id);
        } catch (RuntimeException ignored) {}
        return "redirect:/user/orders";
    }

    // ─── Admin: Manage Orders ─────────────────────────────────────────────────

    @GetMapping("/admin/orders")
    public String adminOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("agents", userService.getUsersByRole(Role.DELIVERY_AGENT));
        return "admin/orders";
    }

    @PostMapping("/admin/orders/assign")
    public String assignAgent(@RequestParam Long orderId,
                               @RequestParam Long agentId, HttpSession session) {
        User admin = (User) session.getAttribute("loggedUser");
        if (admin == null || admin.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        userService.getUserById(agentId).ifPresent(agent -> orderService.assignAgent(orderId, agent));
        return "redirect:/admin/orders";
    }

    // ─── Agent: My Deliveries ─────────────────────────────────────────────────

    @GetMapping("/agent/orders")
    public String agentOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.DELIVERY_AGENT) return "redirect:/login";
        model.addAttribute("orders", orderService.getOrdersByAgent(user));
        return "agent/orders";
    }

    @PostMapping("/agent/orders/update")
    public String updateDeliveryStatus(@RequestParam Long orderId,
                                        @RequestParam OrderStatus status,
                                        HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.DELIVERY_AGENT) return "redirect:/login";
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/agent/orders";
    }

    // ─── Helper methods ───────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCart(HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new java.util.HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    private List<CartItem> buildCartItems(Map<Long, Integer> cart) {
        List<CartItem> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            productService.getProductById(entry.getKey()).ifPresent(product -> {
                CartItem item = new CartItem(product, entry.getValue());
                items.add(item);
            });
        }
        return items;
    }
}