package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Admin.PermissionRequest;
import com.demoJob.demo.dto.response.Admin.PermissionResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.dto.response.system.ResponseError;
import com.demoJob.demo.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public Object create(@RequestBody PermissionRequest request) {
        try {
            PermissionResponse response = permissionService.create(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Tạo quyền thành công", response);
        } catch (Exception e) {
            log.error("Lỗi khi tạo quyền: {}", e.getMessage());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable int id, @RequestBody PermissionRequest request) {
        try {
            PermissionResponse response = permissionService.update(id, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật quyền thành công", response);
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật quyền: {}", e.getMessage());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable int id) {
        try {
            permissionService.delete(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Xoá quyền thành công", null);
        } catch (Exception e) {
            log.error("Lỗi khi xoá quyền: {}", e.getMessage());
            return new ResponseError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Object getById(@PathVariable int id) {
        try {
            PermissionResponse response = permissionService.getById(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Lấy quyền theo ID thành công", response);
        } catch (Exception e) {
            log.error("Lỗi khi lấy quyền theo ID: {}", e.getMessage());
            return new ResponseError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @GetMapping
    public Object getAll() {
        try {
            List<PermissionResponse> responses = permissionService.getAll();
            return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách quyền thành công", responses);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách quyền: {}", e.getMessage());
            return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }
}
