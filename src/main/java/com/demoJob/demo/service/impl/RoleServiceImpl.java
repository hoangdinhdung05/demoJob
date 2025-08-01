package com.demoJob.demo.service.impl;

import com.demoJob.demo.dto.request.Admin.RoleRequest;
import com.demoJob.demo.dto.response.Admin.RoleResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.entity.Role;
import com.demoJob.demo.exception.ResourceNotFoundException;
import com.demoJob.demo.repository.RoleRepository;
import com.demoJob.demo.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Role name already exists");
        }

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        role = roleRepository.save(role);

        log.info("Create role successfully: {}", role.getName());

        return convertToRole(role);
    }

    @Override
    public RoleResponse updateRole(int roleId, RoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        role = roleRepository.save(role);

        log.info("Update role successfully: {}", role.getName());

        return convertToRole(role);
    }

    @Override
    public void deleteRole(int roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }

        roleRepository.deleteById(roleId);
        log.info("Delete role successfully with id: {}", roleId);
    }

    @Override
    public PageResponse<RoleResponse> getRoles(int page, int size) {
        Page<Role> rolePage = roleRepository.findAll(PageRequest.of(page, size));

        List<RoleResponse> roleResponses = rolePage.stream()
                .map(this::convertToRole)
                .toList();

        return PageResponse.<RoleResponse>builder()
                .page(rolePage.getNumber() + 1)
                .size(rolePage.getSize())
                .total(rolePage.getTotalElements())
                .items(roleResponses)
                .build();
    }

    private RoleResponse convertToRole(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}
