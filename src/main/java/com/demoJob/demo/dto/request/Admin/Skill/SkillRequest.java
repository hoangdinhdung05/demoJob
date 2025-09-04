package com.demoJob.demo.dto.request.Admin.Skill;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Yêu cầu tạo hoặc cập nhật kỹ năng")
public class SkillRequest {

    @Schema(description = "Tên kỹ năng", example = "Java")
    @NotBlank(message = "Tên kỹ năng không được để trống")
    private String name;

    @Schema(description = "Mô tả kỹ năng", example = "Kỹ năng lập trình Java")
    private String description;
}
