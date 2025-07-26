package base_webSocket_demo.service;

import base_webSocket_demo.dto.request.Admin.AssignPermissionRequest;
import base_webSocket_demo.entity.Permission;
import java.util.List;

public interface RolePermissionService {
    void assignPermissions(AssignPermissionRequest request);
    List<Permission> getPermissionsByRoleId(Integer roleId);
}
