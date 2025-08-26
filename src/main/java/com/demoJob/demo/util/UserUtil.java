package com.demoJob.demo.util;

import com.demoJob.demo.entity.User;
import com.demoJob.demo.repository.UserRepository;
import com.demoJob.demo.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtil {

    private final UserRepository userRepository;

    /**
     * Lấy user hiện tại đang logic
     */
    public User getCurrentUser() {
        try {
            long userId = SecurityUtils.getCurrentUserId();
            return userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Current user not found with ID: " + userId));
        } catch (NumberFormatException e) {
            throw new SecurityException("Invalid user ID format", e);
        } catch (Exception e) {
            throw new SecurityException("Authentication error: " + e.getMessage(), e);
        }
    }
}