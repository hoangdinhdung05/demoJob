package com.demoJob.demo.dto.request.Admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoleRequest {

    @NotNull(message = "Role is not null")
    private String name;

    private  String description;

}
