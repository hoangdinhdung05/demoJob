package base_webSocket_demo.service.impl;

import base_webSocket_demo.dto.request.Admin.Skill.SkillRequest;
import base_webSocket_demo.dto.response.Admin.SkillResponse;
import base_webSocket_demo.dto.response.system.PageResponse;
import base_webSocket_demo.entity.Skill;
import base_webSocket_demo.repository.SkillRepository;
import base_webSocket_demo.service.SkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    public SkillResponse createSkill(SkillRequest request) {
        Skill skill = Skill.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        skill = skillRepository.save(skill);

        log.info("Create new skill successfully with skill id={}", skill.getId());

        return convertToSkill(skill);
    }

    @Override
    public SkillResponse updateSkill(long skillId, SkillRequest request) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (request.getName() != null) {
            skill.setName(request.getName());
        }

        if (request.getDescription() != null) {
            skill.setDescription(request.getDescription());
        }

        skill = skillRepository.save(skill);

        log.info("Update skill successfully with skill id={}", skill.getId());

        return convertToSkill(skill);
    }

    @Override
    public void delete(long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skillRepository.delete(skill);
    }

    @Override
    public SkillResponse getById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        return convertToSkill(skill);
    }

    @Override
    public List<SkillResponse> searchByName(String keyword) {
        List<Skill> skills = skillRepository.findByNameContainingIgnoreCase(keyword);
        return skills.stream().map(this::convertToSkill).collect(Collectors.toList());
    }

    @Override
    public List<SkillResponse> getAll() {
        List<Skill> lists = skillRepository.findAll();

        return lists.stream()
                .map(this::convertToSkill)
                .toList();
    }

    @Override
    public PageResponse<?> getALlPage(int page, int size) {
        Page<Skill> skillPage = skillRepository.findAll(PageRequest.of(page, size));

        List<SkillResponse> lists = skillPage.stream()
                .map(this::convertToSkill)
                .toList();

        return PageResponse.<SkillResponse>builder()
                .page(skillPage.getNumber() + 1)
                .size(skillPage.getSize())
                .total(skillPage.getTotalElements())
                .items(lists)
                .build();
    }

    //============//=============//

    private SkillResponse convertToSkill(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .description(skill.getDescription())
                .build();
    }

}
