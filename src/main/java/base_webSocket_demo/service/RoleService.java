package base_webSocket_demo.service;

import base_webSocket_demo.dto.request.Admin.RoleRequest;
import base_webSocket_demo.dto.response.Admin.RoleResponse;
import base_webSocket_demo.dto.response.system.PageResponse;

public interface RoleService {

    RoleResponse createRole(RoleRequest request);

    RoleResponse updateRole(int roleId, RoleRequest request);

    void deleteRole(int roleId);

    PageResponse<?> getRoles(int page, int size);

}
