package base_webSocket_demo.dto.request.Admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PermissionRequest {
    @NotBlank
    private String name;

    private String description;
}
