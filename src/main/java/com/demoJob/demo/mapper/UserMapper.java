package com.demoJob.demo.mapper;

import com.demoJob.demo.dto.response.User.UserBasicInfoResponse;
import com.demoJob.demo.dto.response.User.UserFullInfoResponse;
import com.demoJob.demo.dto.response.User.UserUpdateResponse;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserProfile;

public class UserMapper {
    public static UserBasicInfoResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserBasicInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public static UserFullInfoResponse toResponseFullData(User user) {
        UserProfile profile = user.getUserProfile();

        return UserFullInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(profile != null ? profile.getAvatarUrl() : null)
                .phone(profile != null ? profile.getPhone() : null)
                .address(profile != null ? profile.getAddress() : null)
                .gender(profile != null ? profile.getGender() : null)
                .birthDate(profile != null ? profile.getBirthDate() : null)
                .website(profile != null ? profile.getWebsite() : null)
                .build();
    }

    public static UserUpdateResponse toResponseUpdate(User user) {
        UserProfile profile = user.getUserProfile();

        return UserUpdateResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(profile != null ? profile.getAvatarUrl() : null)
                .phone(profile != null ? profile.getPhone() : null)
                .address(profile != null ? profile.getAddress() : null)
                .gender(profile != null ? profile.getGender() : null)
                .birthDate(profile != null ? profile.getBirthDate() : null)
                .website(profile != null ? profile.getWebsite() : null)
                .build();
    }
}
