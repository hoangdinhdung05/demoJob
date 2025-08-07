package com.demoJob.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegisterResponse {
    String email;
    String firstName;
    String lastName;
}
