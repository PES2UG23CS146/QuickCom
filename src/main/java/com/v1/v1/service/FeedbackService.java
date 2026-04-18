package com.v1.v1.service;

import com.v1.v1.model.Feedback;
import com.v1.v1.model.Order;
import com.v1.v1.model.User;
import com.v1.v1.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FeedbackService — manages user feedback for delivered orders.
 * GRASP - Indirection: isolates feedback logic from order and user services.
 */
@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback submitFeedback(Order order, User user, int rating, String comment) {
        Feedback feedback = new Feedback(order, user, rating, comment);
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public List<Feedback> getFeedbackByUser(Long userId) {
        return feedbackRepository.findByUserId(userId);
    }

    public List<Feedback> getFeedbackByOrder(Long orderId) {
        return feedbackRepository.findByOrderId(orderId);
    }
}