package base_webSocket_demo.service.impl;

import base_webSocket_demo.dto.request.Admin.AssignPermissionRequest;
import base_webSocket_demo.entity.Permission;
import base_webSocket_demo.entity.Role;
import base_webSocket_demo.entity.RoleHasPermission;
import base_webSocket_demo.repository.PermissionRepository;
import base_webSocket_demo.repository.RoleHasPermissionRepository;
import base_webSocket_demo.repository.RoleRepository;
import base_webSocket_demo.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleHasPermissionRepository roleHasPermissionRepository;

    @Override
    public void assignPermissions(AssignPermissionRequest request) {
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Xoá hết quyền cũ
        roleHasPermissionRepository.deleteAllByRoleId(role.getId());

        // Gán mới quyền
        Set<RoleHasPermission> rolePermissions = new HashSet<>();
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());

        for (Permission permission : permissions) {
            RoleHasPermission rp = RoleHasPermission.builder()
                    .role(role)
                    .permission(permission)
                    .build();
            rolePermissions.add(rp);
        }

        roleHasPermissionRepository.saveAll(rolePermissions);
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Integer roleId) {
        List<RoleHasPermission> list = roleHasPermissionRepository.findAllByRoleId(roleId);
        return list.stream().map(RoleHasPermission::getPermission).collect(Collectors.toList());
    }
}
