package base_webSocket_demo.service;

import base_webSocket_demo.dto.request.Admin.Skill.SkillRequest;
import base_webSocket_demo.dto.response.Admin.SkillResponse;
import base_webSocket_demo.dto.response.system.PageResponse;
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
