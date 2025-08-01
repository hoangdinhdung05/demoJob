package com.demoJob.demo.dto.response.Admin.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RoleUseResponse {

    private int id;
    private String name;
    private String createdBy;
    private String updatedBy;
}