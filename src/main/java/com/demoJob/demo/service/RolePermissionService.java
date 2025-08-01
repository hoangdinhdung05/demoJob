package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.Admin.AssignPermissionRequest;
import com.demoJob.demo.entity.Permission;
import java.util.List;

public interface RolePermissionService {
    void assignPermissions(AssignPermissionRequest request);
    List<Permission> getPermissionsByRoleId(Integer roleId);
}
