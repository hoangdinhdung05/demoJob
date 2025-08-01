package com.demoJob.demo.util.validator;

import com.demoJob.demo.entity.Job;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JobDateValidator implements ConstraintValidator<ValidDateRange, Job> {

    @Override
    public boolean isValid(Job job, ConstraintValidatorContext context) {
        if (job.getStartDate() == null || job.getEndDate() == null) return true; // để tránh lỗi null
        return !job.getEndDate().isBefore(job.getStartDate());
    }
}
