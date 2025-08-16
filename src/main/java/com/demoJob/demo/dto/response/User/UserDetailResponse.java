package com.demoJob.demo.dto.response.User;

import com.demoJob.demo.util.Gender;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class UserDetailResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;
    private String phone;
    private String address;
    private Gender gender;
    private LocalDate birthDate;
    private String website;

    private CompanyResponse company; // Thêm thông tin công ty nếu cần
}
