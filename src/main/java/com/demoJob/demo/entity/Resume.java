package com.demoJob.demo.entity;

import com.demoJob.demo.util.ResumeStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "tbl_resume")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Resume extends AbstractEntity<Long> {
    @NotBlank(message = "email không được để trống")
    private String email;

    @NotBlank(message = "url không được để trống (upload cv chưa thành công)")
    private String url;

    @Enumerated(EnumType.STRING)
    private ResumeStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

}
