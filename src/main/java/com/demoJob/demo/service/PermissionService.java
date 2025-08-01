package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.Admin.PermissionRequest;
import com.demoJob.demo.dto.response.Admin.PermissionResponse;
import java.util.List;

public interface PermissionService {
    PermissionResponse create(PermissionRequest request);

    PermissionResponse update(int id, PermissionRequest request);

    void delete(int id);

    PermissionResponse getById(int id);

    List<PermissionResponse> getAll();
}
