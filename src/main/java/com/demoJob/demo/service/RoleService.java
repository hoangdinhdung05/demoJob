package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.Admin.RoleRequest;
import com.demoJob.demo.dto.response.Admin.RoleResponse;
import com.demoJob.demo.dto.response.system.PageResponse;

public interface RoleService {

    RoleResponse createRole(RoleRequest request);

    RoleResponse updateRole(int roleId, RoleRequest request);

    void deleteRole(int roleId);

    PageResponse<?> getRoles(int page, int size);

}
