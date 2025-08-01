package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.request.Admin.PermissionRequest;
import com.demoJob.demo.dto.response.Admin.PermissionResponse;
import com.demoJob.demo.entity.Permission;
import com.demoJob.demo.repository.PermissionRepository;
import com.demoJob.demo.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = Permission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        permission = permissionRepository.save(permission);
        return convertToResponse(permission);
    }

    @Override
    public PermissionResponse update(int id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());

        return convertToResponse(permissionRepository.save(permission));
    }

    @Override
    public void delete(int id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        permissionRepository.delete(permission);
    }

    @Override
    public PermissionResponse getById(int id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        return convertToResponse(permission);
    }

    @Override
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private PermissionResponse convertToResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}
