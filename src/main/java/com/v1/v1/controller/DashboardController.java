package com.v1.v1.controller;

import com.v1.v1.model.Role;
import com.v1.v1.model.User;
import com.v1.v1.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * DashboardController routes users to their role-specific dashboards.
 * GRASP - Protected Variations: role routing is centralized, shielding views from role logic.
 * SOLID - SRP: Only manages dashboard navigation.
 */
@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    // ─── User Dashboard ───────────────────────────────────────────────────────
    @GetMapping("/user/dashboard")
    public String userDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.USER) return "redirect:/login";
        model.addAttribute("user", user);
        return "user/dashboard";
    }

    // ─── Warehouse Admin Dashboard ────────────────────────────────────────────
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("agents", userService.getUsersByRole(Role.DELIVERY_AGENT));
        return "admin/dashboard";
    }

    // ─── Delivery Agent Dashboard ─────────────────────────────────────────────
    @GetMapping("/agent/dashboard")
    public String agentDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.DELIVERY_AGENT) return "redirect:/login";
        model.addAttribute("user", user);
        return "agent/dashboard";
    }
}