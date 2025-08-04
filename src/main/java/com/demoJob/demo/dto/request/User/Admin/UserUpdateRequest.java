package com.demoJob.demo.dto.request.User.Admin;

import com.demoJob.demo.util.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    private String firstName;
    private String lastName;
    private String phone;

    @NotNull
    private UserStatus status;
}
