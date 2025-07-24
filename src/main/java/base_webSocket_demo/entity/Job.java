package base_webSocket_demo.entity;

import base_webSocket_demo.util.JobStatus;
import base_webSocket_demo.util.LevelEnum;
import base_webSocket_demo.util.validator.ValidDateRange;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tbl_jobs")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ValidDateRange
public class Job extends AbstractEntity<Long> {

    @NotBlank(message = "Name job is not null")
    private String name;

    private String location;

    @Min(value = 0, message = "Lương không được âm")
    private double salary;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private int quantity;

    @Enumerated(EnumType.STRING)
    private LevelEnum level;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @NotNull(message = "Ngày bắt đầu không được null")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được null")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobStatus status;

    @ManyToOne
    @JoinColumn( name = "company_id")
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"jobs"})
    @JoinTable(name = "tbl_job_skill", joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Resume> resumes;

}
