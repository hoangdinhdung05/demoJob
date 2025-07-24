package base_webSocket_demo.dto.response.Admin.Resume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ResumeCreateResponse {

    private Long id;
    private String email;
    private String createdBy;
    private LocalDateTime createdAt;

}
