package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Admin.AssignPermissionRequest;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.dto.response.system.ResponseError;
import com.demoJob.demo.entity.Permission;
import com.demoJob.demo.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/role-permission")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @PostMapping("/assign-permissions")
    public Object assignPermissions(@RequestBody AssignPermissionRequest request) {
        try {
            rolePermissionService.assignPermissions(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Gán quyền cho role thành công", null);
        } catch (Exception e) {
            log.error("Lỗi khi gán quyền cho role: {}", e.getMessage());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/{roleId}/permissions")
    public Object getPermissionsByRole(@PathVariable Integer roleId) {
        try {
            List<Permission> permissions = rolePermissionService.getPermissionsByRoleId(roleId);
            return new ResponseData<>(HttpStatus.OK.value(), "Danh sách quyền theo role", permissions);
        } catch (Exception e) {
            log.error("Lỗi khi lấy quyền theo role: {}", e.getMessage());
            return new ResponseError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }
}
