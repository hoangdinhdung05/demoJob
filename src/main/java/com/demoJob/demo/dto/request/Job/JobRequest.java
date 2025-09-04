package com.demoJob.demo.dto.request.Job;

import com.demoJob.demo.util.enums.LevelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Schema(description = "Yêu cầu tạo hoặc cập nhật công việc")
public class JobRequest {

    @Schema(description = "Tên công việc", example = "Lập trình viên Java")
    @NotBlank(message = "Tên job không được để trống")
    private String name;

    @Schema(description = "Vị trí công việc", example = "Hà Nội")
    private String location;

    @Schema(description = "Lương", example = "15000000")
    @Min(value = 0, message = "Lương không được âm")
    private double salary;

    @Schema(description = "Số lượng tuyển dụng", example = "5")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    @Max(value = 50, message = "Số lượng không lớn hơn 50")
    private int quantity;

    @Schema(description = "Trình độ", example = "JUNIOR, MID, SENIOR")
    private LevelEnum level;

    @Schema(description = "Mô tả công việc", example = "Phát triển và bảo trì các ứng dụng Java.")
    private String description;

    @Schema(description = "Ngày bắt đầu", example = "2024-07-01")
    @NotNull(message = "Ngày bắt đầu không được null")
    private LocalDate startDate;

    @Schema(description = "Ngày kết thúc", example = "2024-12-31")
    @NotNull(message = "Ngày kết thúc không được null")
    private LocalDate endDate;

    @Schema(description = "ID công ty", example = "1")
    @NotNull(message = "Phải có ID công ty")
    private Long companyId;

    @Schema(description = "Danh sách ID kỹ năng", example = "[1, 2, 3]")
    private List<Long> skillIds;
}
