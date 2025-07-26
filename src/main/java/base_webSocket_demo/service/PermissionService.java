package base_webSocket_demo.service;

import base_webSocket_demo.dto.request.Admin.PermissionRequest;
import base_webSocket_demo.dto.response.Admin.PermissionResponse;
import java.util.List;

public interface PermissionService {
    PermissionResponse create(PermissionRequest request);

    PermissionResponse update(int id, PermissionRequest request);

    void delete(int id);

    PermissionResponse getById(int id);

    List<PermissionResponse> getAll();
}
