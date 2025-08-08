package com.demoJob.demo.mapper;

import com.demoJob.demo.dto.response.User.AdminUserResponse;
import com.demoJob.demo.entity.Role;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserProfile;
import java.util.stream.Collectors;

public class AdminUserMapper {

    public static AdminUserResponse toResponse(User user) {
        UserProfile profile = user.getUserProfile();

        return AdminUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(profile != null ? profile.getPhone() : null)
                .status(user.getStatus())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

}
