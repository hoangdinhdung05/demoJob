package base_webSocket_demo.dto.request.Admin;

import lombok.Getter;
import java.util.List;

@Getter
public class AssignPermissionRequest {
    private Integer roleId;
    private List<Integer> permissionIds;
}
