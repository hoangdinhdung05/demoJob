package base_webSocket_demo.dto.response.Admin.Job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CompanyJobResponse {

    private long id;
    private String name;

}
