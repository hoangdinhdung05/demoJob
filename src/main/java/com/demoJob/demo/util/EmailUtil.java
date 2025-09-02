package com.demoJob.demo.util;

import com.demoJob.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    public String getDisplayName(User user) {
        if (user.getFirstName() != null || user.getLastName() != null) {
            return (user.getFirstName() == null ? "" : user.getFirstName()) +
                    " " +
                    (user.getLastName() == null ? "" : user.getLastName());
        }
        return user.getUsername() != null ? user.getUsername() : user.getEmail();
    }

}
