package com.demoJob.demo.util;

import com.demoJob.demo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final TemplateEngine templateEngine;

    public String getDisplayName(User user) {
        if (user.getFirstName() != null || user.getLastName() != null) {
            return (user.getFirstName() == null ? "" : user.getFirstName()) +
                    " " +
                    (user.getLastName() == null ? "" : user.getLastName());
        }
        return user.getUsername() != null ? user.getUsername() : user.getEmail();
    }
}
