package com.demoJob.demo.dto.request.User.Client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "User Account Update Request")
public class UserAccountUpdateRequest {
    @Schema(description = "First name of the user", example = "John")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String firstName;

    @Schema(description = "Last name of the user", example = "Doe")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String lastName;

    @Schema(description = "Email of the user", example = "example@gamil.com")
    @Email(message = "Email should be valid")
    private String email;
}