package com.v1.v1.controller;

import com.v1.v1.model.Role;
import com.v1.v1.model.User;
import com.v1.v1.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Handles authentication: register and login.
 * SOLID - SRP: Only responsible for auth flows (register/login/logout).
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("user") User user,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "register";
        }
        try {
            userService.registerUser(user);
            return "redirect:/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", Role.values());
            return "register";
        }
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String registered, Model model) {
        if (registered != null) model.addAttribute("success", "Registration successful! Please login.");
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session, Model model) {
        return userService.login(email, password).map(user -> {
            session.setAttribute("loggedUser", user);
            // GRASP - Protected Variations: role-based redirect insulates routing from role changes
            return switch (user.getRole()) {
                case WAREHOUSE_ADMIN -> "redirect:/admin/dashboard";
                case DELIVERY_AGENT -> "redirect:/agent/dashboard";
                default -> "redirect:/user/dashboard";
            };
        }).orElseGet(() -> {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        });
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}