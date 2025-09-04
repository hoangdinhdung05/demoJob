package com.demoJob.demo.dto.request.Resume;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "Yêu cầu nộp CV")
public class ResumeRequest {

    @Schema(description = "ID công việc", example = "1")
    private Long jobId;

    @Schema(description = "Email của ứng viên", example = "exmaple@gmail.com")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Schema(description = "URL của bản CV", example = "http://example.com/resume.pdf")
    @Size(max = 255, message = "URL không được vượt quá 255 ký tự")
    private String url;
}