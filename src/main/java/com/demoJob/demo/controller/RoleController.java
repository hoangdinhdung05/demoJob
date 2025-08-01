package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Admin.RoleRequest;
import com.demoJob.demo.dto.response.Admin.RoleResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.dto.response.system.ResponseError;
import com.demoJob.demo.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/admin/create")
    public ResponseData<?> createRole(@RequestBody @Valid RoleRequest request) {
        try {
            RoleResponse response = roleService.createRole(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Create role successfully", response);
        } catch (Exception e) {
            log.error("Create role failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PutMapping("/admin/{roleId}")
    public ResponseData<?> updateRole(@PathVariable int roleId, @RequestBody @Valid RoleRequest request) {
        try {
            RoleResponse response = roleService.updateRole(roleId, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Update role successfully", response);
        } catch (Exception e) {
            log.error("Update role failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @DeleteMapping("/admin/{roleId}")
    public ResponseData<?> deleteRole(@PathVariable int roleId) {
        try {
            roleService.deleteRole(roleId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete role successfully");
        } catch (Exception e) {
            log.error("Delete role failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/admin/getAll")
    public ResponseData<?> getRoles(@RequestParam int page, @RequestParam int size) {
        try {
            PageResponse<?> response = roleService.getRoles(page, size);
            return new ResponseData<>(HttpStatus.OK.value(), "Get roles successfully", response);
        } catch (Exception e) {
            log.error("Get roles failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get roles failed");
        }
    }
}
