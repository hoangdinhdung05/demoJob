package com.demoJob.demo.dto.response.Admin.Resume;

import com.demoJob.demo.util.ResumeStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResumeResponse {
    private long id;
    private String email;
    private String url;
    @Enumerated(EnumType.STRING)
    private ResumeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String companyName;
    private UserResume user;
    private JobResume job;

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class UserResume {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class JobResume {
        private long id;
        private String name;
    }
}
