package com.demoJob.demo.dto.response.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SkillResponse {
    private Long id;
    private String name;
    private String description;
}
