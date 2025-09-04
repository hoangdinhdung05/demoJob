package com.demoJob.demo.dto.request.Job;

import com.demoJob.demo.util.enums.JobStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Yêu cầu cập nhật trạng thái công việc")
public class JobStatusRequest {
    @Schema(description = "Trạng thái công việc", example = "OPEN, CLOSED, ACTIVE")
    private JobStatus status;
}
