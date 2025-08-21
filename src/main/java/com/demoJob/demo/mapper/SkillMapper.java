package com.demoJob.demo.mapper;

import com.demoJob.demo.dto.response.Admin.SkillResponse;
import com.demoJob.demo.entity.Skill;

public class SkillMapper {

    public static SkillResponse toResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .description(skill.getDescription())
                .build();
    }

}
