package com.v1.v1.controller;

import com.v1.v1.model.Role;
import com.v1.v1.model.User;
import com.v1.v1.service.RoleActionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * SOLID - Interface Segregation Principle:
 * AgentActionController exposes only delivery-agent-relevant endpoints.
 * No admin or user endpoints are mixed in here.
 *
 * GRASP - Controller:
 * Handles system events (delivery updates) on behalf of the DeliveryAgent role.
 */
@Controller
@RequestMapping("/agent")
public class AgentActionController {

    @Autowired
    private RoleActionService roleActionService;

    @PostMapping("/deliver/{orderId}")
    public String markDelivered(@PathVariable Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.DELIVERY_AGENT) return "redirect:/login";
        roleActionService.agentMarkDelivered(orderId);
        return "redirect:/agent/orders";
    }

    @PostMapping("/outfordelivery/{orderId}")
    public String markOutForDelivery(@PathVariable Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null || user.getRole() != Role.DELIVERY_AGENT) return "redirect:/login";
        roleActionService.agentMarkOutForDelivery(orderId);
        return "redirect:/agent/orders";
    }
}