package com.demoJob.demo.dto.response.User;

import com.demoJob.demo.util.UserStatus;
import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class AdminUserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UserStatus status;
    private Set<String> roles;
}