package com.demoJob.demo.dto.request.Admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Role Request")
public class RoleRequest {

    @Schema(description = "Role name", example = "ADMIN")
    @NotNull(message = "Role is not null")
    private String name;

    @Schema(description = "Role description", example = "Administrator role with full permissions")
    private  String description;

}
