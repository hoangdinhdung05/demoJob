package base_webSocket_demo.dto.response.Admin.Resume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResumeUpdateResponse {

    private long id;
    private String updatedBy;
    private LocalDateTime updatedAt;

}
