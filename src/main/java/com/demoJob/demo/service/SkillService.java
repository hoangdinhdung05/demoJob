package com.demoJob.demo.service;

import com.demoJob.demo.dto.request.Admin.Skill.SkillRequest;
import com.demoJob.demo.dto.response.Admin.SkillResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import java.util.List;

public interface SkillService {

    SkillResponse createSkill(SkillRequest request);

    SkillResponse updateSkill(long skillId, SkillRequest request);

    void delete(long skillId);

    SkillResponse getById(Long id);

    List<SkillResponse> searchByName(String keyword);

    List<SkillResponse> getAll();

    PageResponse<?> getALlPage(int page, int size);

}
