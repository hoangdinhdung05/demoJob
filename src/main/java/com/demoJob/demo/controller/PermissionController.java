package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Admin.PermissionRequest;
import com.demoJob.demo.dto.response.Admin.PermissionResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.dto.response.system.ResponseError;
import com.demoJob.demo.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "ADMIN-PERMISSION", description = "Quản lý quyền - Admin")
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * Admin tạo quyền mới.
     * @param request Thông tin quyền mới.
     * @return ResponseData chứa mã trạng thái và thông tin quyền đã tạo.
     */
    @Operation(summary = "Tạo quyền mới", description = "Admin tạo quyền mới.")
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

    /**
     * Admin cập nhật quyền hiện có.
     * @param id ID của quyền cần cập nhật.
     * @param request Thông tin cập nhật quyền.
     * @return ResponseData chứa mã trạng thái và thông tin quyền đã cập nhật.
     */
    @Operation(summary = "Cập nhật quyền", description = "Admin cập nhật quyền hiện có.")
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

    /**
     * Admin xoá quyền hiện có.
     * @param id ID của quyền cần xoá.
     * @return ResponseData chứa mã trạng thái và thông báo xoá thành công.
     */
    @Operation(summary = "Xoá quyền", description = "Admin xoá quyền hiện có.")
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

    /**
     * Lấy thông tin chi tiết quyền theo ID.
     * @param id ID của quyền cần lấy.
     * @return ResponseData chứa mã trạng thái và thông tin quyền.
     */
    @Operation(summary = "Lấy quyền theo ID", description = "Lấy thông tin chi tiết quyền theo ID.")
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

    /**
     * Lấy danh sách tất cả quyền.
     * @return ResponseData chứa mã trạng thái và danh sách quyền.
     */
    @Operation(summary = "Lấy tất cả quyền", description = "Lấy danh sách tất cả quyền.")
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
