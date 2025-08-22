package com.demoJob.demo.mapper;

import com.demoJob.demo.dto.request.Job.JobRequest;
import com.demoJob.demo.dto.response.Job.JobResponse;
import com.demoJob.demo.entity.Company;
import com.demoJob.demo.entity.Job;
import com.demoJob.demo.entity.Skill;
import com.demoJob.demo.util.enums.JobStatus;

import java.util.List;
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

    public static Job buildJob(JobRequest request, Company company, List<Skill> skillList, boolean isAdmin) {
        return Job.builder()
                .name(request.getName().trim())
                .location(request.getLocation() != null ? request.getLocation().trim() : null)
                .salary(request.getSalary())
                .quantity(request.getQuantity())
                .level(request.getLevel())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(isAdmin ? JobStatus.ACTIVE : JobStatus.PENDING)
                .company(company)
                .skills(skillList)
                .build();
    }

}
