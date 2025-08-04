package com.demoJob.demo.dto.request.User.Admin;

import lombok.Builder;
import lombok.Getter;
import java.util.Set;

@Getter
@Builder
public class UpdateUserRolesRequest {
    private Set<String> roles;
}
