package com.demoJob.demo.dto.request.Job;

import com.demoJob.demo.util.enums.JobStatus;
import lombok.Getter;

@Getter
public class JobStatusRequest {
    private JobStatus status;
}
