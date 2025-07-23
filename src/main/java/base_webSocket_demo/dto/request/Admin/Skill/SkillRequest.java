package base_webSocket_demo.dto.request.Admin.Skill;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillRequest {
    @NotBlank(message = "Tên kỹ năng không được để trống")
    private String name;

    private String description;
}
