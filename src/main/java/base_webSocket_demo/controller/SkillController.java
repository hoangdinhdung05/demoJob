package base_webSocket_demo.controller;

import base_webSocket_demo.dto.request.Admin.Skill.SkillRequest;
import base_webSocket_demo.dto.response.Admin.SkillResponse;
import base_webSocket_demo.dto.response.system.PageResponse;
import base_webSocket_demo.dto.response.system.ResponseData;
import base_webSocket_demo.dto.response.system.ResponseError;
import base_webSocket_demo.service.SkillService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@Slf4j
public class SkillController {

    private final SkillService skillService;

    @PostMapping("/admin/create")
    public ResponseData<?> createSkill(@RequestBody @Valid SkillRequest request) {
        log.info("API admin create skill");

        try {
            SkillResponse response = skillService.createSkill(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Create skill successfully", response);
        } catch (Exception e) {
            log.error("Create skill failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Create skill failed");
        }
    }

    @PatchMapping("/admin/{skillId}")
    public ResponseData<?> updateSkill(@PathVariable @Min(1) Long skillId,
                                       @RequestBody @Valid SkillRequest request) {
        log.info("API admin update skill ID={}", skillId);

        try {
            SkillResponse response = skillService.updateSkill(skillId, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Update skill successfully", response);
        } catch (Exception e) {
            log.error("Update skill failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update skill failed");
        }
    }

    @DeleteMapping("/admin/{skillId}")
    public ResponseData<?> deleteSkill(@PathVariable @Min(1) Long skillId) {
        log.info("API admin delete skill ID={}", skillId);

        try {
            skillService.delete(skillId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete skill successfully");
        } catch (Exception e) {
            log.error("Delete skill failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete skill failed");
        }
    }

    @GetMapping("/admin/{skillId}")
    public ResponseData<?> getSkillById(@PathVariable @Min(1) Long skillId) {
        log.info("API admin get skill by ID={}", skillId);

        try {
            SkillResponse response = skillService.getById(skillId);
            return new ResponseData<>(HttpStatus.OK.value(), "Get skill successfully", response);
        } catch (Exception e) {
            log.error("Get skill failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get skill failed");
        }
    }

    @GetMapping("/admin/search")
    public ResponseData<?> searchSkill(@RequestParam String keyword) {
        log.info("API admin search skill by keyword='{}'", keyword);

        try {
            List<SkillResponse> response = skillService.searchByName(keyword);
            return new ResponseData<>(HttpStatus.OK.value(), "Search skill successfully", response);
        } catch (Exception e) {
            log.error("Search skill failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Search skill failed");
        }
    }

    @GetMapping("/admin/getAll")
    public ResponseData<?> getAllSkills() {
        log.info("API admin get all skills");

        try {
            List<SkillResponse> response = skillService.getAll();
            return new ResponseData<>(HttpStatus.OK.value(), "Get all skills successfully", response);
        } catch (Exception e) {
            log.error("Get all skills failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get all skills failed");
        }
    }

    @GetMapping("/admin/getAllPage")
    public ResponseData<?> getAllSkillsPage(@RequestParam int page, @RequestParam int size) {
        log.info("API admin get paginated skills");

        try {
            PageResponse<?> response = skillService.getALlPage(page, size);
            return new ResponseData<>(HttpStatus.OK.value(), "Get skills page successfully", response);
        } catch (Exception e) {
            log.error("Get skills page failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get skills page failed");
        }
    }
}
