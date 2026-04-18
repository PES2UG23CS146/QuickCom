package com.v1.v1.service;

import com.v1.v1.model.Role;
import com.v1.v1.model.User;
import com.v1.v1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * SOLID - Single Responsibility Principle:
 * UserService is solely responsible for user-related business logic:
 * registration, login validation, and role-based retrieval.
 * It delegates persistence to UserRepository.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Registers a new user after checking for duplicate emails.
     * GRASP - Information Expert: Service knows what it takes to register a user.
     */
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered: " + user.getEmail());
        }
        // In production, encode password. Here stored plain for simplicity.
        return userRepository.save(user);
    }

    /**
     * Authenticates user by email and password.
     */
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password) && u.isActive());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void deactivateUser(Long id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setActive(false);
            userRepository.save(u);
        });
    }
}