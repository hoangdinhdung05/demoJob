package com.demoJob.demo.dto.request.Job;

import com.demoJob.demo.util.enums.JobStatus;
import com.demoJob.demo.util.enums.LevelEnum;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
public class JobUpdateRequest {
    private Long id;
    private String name;
    private String location;
    private BigDecimal salary;
    private int quantity;
    private LevelEnum level;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private JobStatus status;
    private List<Long> skillIds;
}
