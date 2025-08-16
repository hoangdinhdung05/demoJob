package com.demoJob.demo.mapper;

import com.demoJob.demo.dto.response.User.CompanyResponse;
import com.demoJob.demo.dto.response.User.UserDetailResponse;
import com.demoJob.demo.dto.response.User.UserInfoResponse;
import com.demoJob.demo.dto.response.User.UserUpdateResponse;
import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.UserCompany;
import com.demoJob.demo.entity.UserProfile;

import java.util.Set;

public class UserMapper {

    public static UserInfoResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public static UserDetailResponse toResponseFullData(User user) {
        UserProfile profile = user.getUserProfile();

        Set<UserCompany> userCompanies = user.getUserCompanies();

        CompanyResponse companyResponse = userCompanies.stream()
                .filter(uc -> uc.getEndDate() == null) // công ty hiện tại (chưa có endDate)
                .findFirst()
                .map(company -> CompanyResponse.builder()
                        .id(company.getCompany().getId())
                        .name(company.getCompany().getName())
                        .build())
                .orElse(null);

        return UserDetailResponse.builder()
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
                .company(companyResponse)
                .build();
    }

    public static UserUpdateResponse toResponseUpdate(User user) {
        UserProfile profile = user.getUserProfile();

        CompanyResponse currentCompany = user.getUserCompanies().stream()
                .filter(uc -> uc.getEndDate() == null) // công ty hiện tại (chưa có endDate)
                .map(uc -> CompanyResponse.builder()
                        .id(uc.getCompany().getId())
                        .name(uc.getCompany().getName())
                        .build())
                .findFirst()
                .orElse(null);

        return UserUpdateResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .avatarUrl(profile != null ? profile.getAvatarUrl() : null)
                .phone(profile != null ? profile.getPhone() : null)
                .address(profile != null ? profile.getAddress() : null)
                .gender(profile != null ? profile.getGender() : null)
                .birthDate(profile != null ? profile.getBirthDate() : null)
                .website(profile != null ? profile.getWebsite() : null)
                .companyId(currentCompany != null ? currentCompany.getId() : null)
                .companyName(currentCompany != null ? currentCompany.getName() : null)
                .build();
    }
}
