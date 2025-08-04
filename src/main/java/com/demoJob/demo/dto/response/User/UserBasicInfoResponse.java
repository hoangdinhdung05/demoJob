package com.demoJob.demo.dto.response.User;

import com.demoJob.demo.util.Gender;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBasicInfoResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
