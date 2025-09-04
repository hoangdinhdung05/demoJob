package com.demoJob.demo.dto.request.Admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.util.List;

@Getter
@Schema(description = "Yêu cầu gán quyền cho vai trò")
public class AssignPermissionRequest {

    @Schema(description = "ID của vai trò", example = "1")
    private Integer roleId;

    @Schema(description = "Danh sách ID quyền", example = "[1, 2, 3]")
    private List<Integer> permissionIds;
}
