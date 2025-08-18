package com.demoJob.demo.dto.request.User.Admin;

import com.demoJob.demo.util.UserStatus;
import lombok.Data;
import java.util.Set;

@Data
public class UserAdminUpdateRequest {

    private String firstName;

    private String lastName;

    private String email;

    private Set<String> roles;

    private Boolean emailVerified;

    private UserStatus status;
}