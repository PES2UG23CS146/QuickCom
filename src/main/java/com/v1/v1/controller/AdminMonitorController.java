package com.v1.v1.controller;

import com.v1.v1.model.Role;
import com.v1.v1.model.User;
import com.v1.v1.service.AdminDashboardData;
import com.v1.v1.service.RoleActionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * GRASP - Controller (GRASP pattern):
 * AdminMonitorController handles all admin system-event requests,
 * acting as the system facade for admin operations.
 *
 * GRASP - Pure Fabrication:
 * Uses RoleActionService — a fabricated service — to serve aggregated data.
 *
 * SOLID - Interface Segregation Principle:
 * This controller only exposes endpoints relevant to WAREHOUSE_ADMIN.
 * Agents and Users have entirely separate controllers.
 */
@Controller
public class AdminMonitorController {

    @Autowired
    private RoleActionService roleActionService;

    /**
     * Admin monitoring dashboard — aggregated system view.
     */
    @GetMapping("/admin/monitor")
    public String monitorDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        AdminDashboardData data = roleActionService.getAdminDashboardData();
        model.addAttribute("data", data);
        model.addAttribute("user", user);
        return "admin/monitor";
    }

    @PostMapping("/admin/agent/outfordelivery/{orderId}")
    public String markOutForDelivery(@PathVariable Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.WAREHOUSE_ADMIN) return "redirect:/login";
        roleActionService.agentMarkOutForDelivery(orderId);
        return "redirect:/admin/orders";
    }
}