package com.demoJob.demo.dto.request.Admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "Permission Request")
public class PermissionRequest {

    @Schema(description = "Name of the permission", example = "READ_PRIVILEGES")
    @NotBlank
    private String name;

    @Schema(description = "Description of the permission", example = "Allows reading of privileges")
    private String description;
}
