package com.demoJob.demo.dto.request.Admin.Resume;

import lombok.Getter;

@Getter
public class ResumeRequest {
    private String email;
    private String url;
    private long userId;
    private long jobId;
}
