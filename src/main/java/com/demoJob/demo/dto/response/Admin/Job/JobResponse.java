package com.demoJob.demo.dto.response.Admin.Job;

import com.demoJob.demo.dto.response.Admin.SkillResponse;
import com.demoJob.demo.util.JobStatus;
import com.demoJob.demo.util.LevelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private JobStatus status;
    private CompanyJobResponse company;
    private Set<SkillResponse> skills;
}
