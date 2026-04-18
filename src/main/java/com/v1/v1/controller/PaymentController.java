package com.v1.v1.controller;

import com.v1.v1.model.*;
import com.v1.v1.service.FeedbackService;
import com.v1.v1.service.OrderService;
import com.v1.v1.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * PaymentController — handles payment, refund, and feedback flows.
 * GRASP - Indirection: Controller delegates to PaymentService — no direct DB access.
 * SOLID - LSP: PaymentMethod enum variants are interchangeable in processPayment calls.
 */
@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private FeedbackService feedbackService;

    // ─── User: Pay for Order ──────────────────────────────────────────────────

    @GetMapping("/user/pay/{orderId}")
    public String paymentForm(@PathVariable Long orderId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.USER) return "redirect:/login";
        orderService.getOrderById(orderId).ifPresent(o -> model.addAttribute("order", o));
        model.addAttribute("methods", PaymentMethod.values());
        return "user/payment";
    }

    @PostMapping("/user/pay/{orderId}")
    public String processPayment(@PathVariable Long orderId,
                                  @RequestParam PaymentMethod method,
                                  HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.USER) return "redirect:/login";
        try {
            Order order = orderService.getOrderById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            paymentService.processPayment(order, method);
            return "redirect:/user/orders?paid=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/user/orders";
        }
    }

    // ─── User: Submit Feedback ────────────────────────────────────────────────

    @GetMapping("/user/feedback/{orderId}")
    public String feedbackForm(@PathVariable Long orderId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.USER) return "redirect:/login";
        orderService.getOrderById(orderId).ifPresent(o -> model.addAttribute("order", o));
        return "user/feedback";
    }

    @PostMapping("/user/feedback/{orderId}")
    public String submitFeedback(@PathVariable Long orderId,
                                  @RequestParam int rating,
                                  @RequestParam String comment,
                                  HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.USER) return "redirect:/login";
        Order order = orderService.getOrderById(orderId).orElse(null);
        if (order != null) {
            feedbackService.submitFeedback(order, user, rating, comment);
        }
        return "redirect:/user/orders?feedback=true";
    }

    // ─── Admin: View Payments & Authorize Refunds ─────────────────────────────

    @GetMapping("/admin/payments")
    public String adminPayments(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        model.addAttribute("payments", paymentService.getAllPayments());
        return "admin/payments";
    }

    @GetMapping("/admin/payments/refund/{paymentId}")
    public String authorizeRefund(@PathVariable Long paymentId, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        paymentService.authorizeRefund(paymentId);
        return "redirect:/admin/payments";
    }

    // ─── Admin: View All Feedback ─────────────────────────────────────────────

    @GetMapping("/admin/feedback")
    public String adminFeedback(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        model.addAttribute("feedbacks", feedbackService.getAllFeedback());
        return "admin/feedback";
    }
}