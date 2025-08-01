package com.demoJob.demo.dto.request.Admin;

import lombok.Getter;
import java.util.List;

@Getter
public class AssignPermissionRequest {
    private Integer roleId;
    private List<Integer> permissionIds;
}
