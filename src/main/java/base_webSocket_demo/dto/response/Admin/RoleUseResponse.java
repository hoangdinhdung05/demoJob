package base_webSocket_demo.dto.response.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RoleUseResponse {

    private int id;
    private String name;
}