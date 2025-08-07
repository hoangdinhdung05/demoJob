package com.demoJob.demo.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class UserDTO {
    //Sửa đổi chỉ cần response ra những thông tin cần thiết
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles;
}