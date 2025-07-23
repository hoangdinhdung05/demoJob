package base_webSocket_demo.dto.response.Admin.Job;

import base_webSocket_demo.dto.response.Admin.SkillResponse;
import base_webSocket_demo.util.JobStatus;
import base_webSocket_demo.util.LevelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
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
