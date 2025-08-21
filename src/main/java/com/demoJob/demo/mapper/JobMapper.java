package com.demoJob.demo.mapper;

import com.demoJob.demo.dto.response.Admin.Job.JobResponse;
import com.demoJob.demo.entity.Job;

import java.util.stream.Collectors;

public class JobMapper {

    public static JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .name(job.getName())
                .location(job.getLocation())
                .salary(job.getSalary())
                .quantity(job.getQuantity())
                .level(job.getLevel())
                .description(job.getDescription())
                .startDate(job.getStartDate())
                .endDate(job.getEndDate())
                .company(CompanyMapper.toResponseJob(job.getCompany()))
                .skills(job.getSkills().stream()
                        .map(SkillMapper::toResponse)
                        .collect(Collectors.toSet()))
                .status(job.getStatus())
                .build();
    }

}