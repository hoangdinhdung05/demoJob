package base_webSocket_demo.dto.request.Admin.Job;

import base_webSocket_demo.util.JobStatus;
import base_webSocket_demo.util.LevelEnum;
import lombok.Getter;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Getter
public class JobRequest {

    @NotBlank(message = "Tên job không được để trống")
    private String name;

    private String location;

    @Min(value = 0, message = "Lương không được âm")
    private double salary;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private int quantity;

    private LevelEnum level;

    private String description;

    @NotNull(message = "Ngày bắt đầu không được null")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được null")
    private LocalDate endDate;

    @NotNull(message = "Trạng thái job không được để trống")
    private JobStatus status;

    @NotNull(message = "Phải có ID công ty")
    private Long companyId;

    private List<Long> skillIds;

}
