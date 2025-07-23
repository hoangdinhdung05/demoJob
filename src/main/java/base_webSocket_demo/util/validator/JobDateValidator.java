package base_webSocket_demo.util.validator;

import base_webSocket_demo.entity.Job;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JobDateValidator implements ConstraintValidator<ValidDateRange, Job> {

    @Override
    public boolean isValid(Job job, ConstraintValidatorContext context) {
        if (job.getStartDate() == null || job.getEndDate() == null) return true; // để tránh lỗi null
        return !job.getEndDate().isBefore(job.getStartDate());
    }
}
