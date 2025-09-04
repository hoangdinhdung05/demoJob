package com.demoJob.demo.controller;

import com.demoJob.demo.dto.request.Admin.Skill.SkillRequest;
import com.demoJob.demo.dto.response.Admin.SkillResponse;
import com.demoJob.demo.dto.response.system.PageResponse;
import com.demoJob.demo.dto.response.system.ResponseData;
import com.demoJob.demo.dto.response.system.ResponseError;
import com.demoJob.demo.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "ADMIN-SKILL", description = "Quản lý kỹ năng - Admin")
public class SkillController {

    private final SkillService skillService;

    /**
     * Admin tạo kỹ năng mới.
     * @param request Thông tin kỹ năng mới.
     * @return ResponseData chứa mã trạng thái và thông tin kỹ năng đã tạo.
     */
    @Operation(summary = "Create a new skill", description = "Admin tạo kỹ năng mới.")
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

    /**
     * Admin cập nhật kỹ năng hiện có.
     * @param skillId ID của kỹ năng cần cập nhật.
     * @param request Thông tin cập nhật kỹ năng.
     * @return ResponseData chứa mã trạng thái và thông tin kỹ năng đã cập nhật.
     */
    @Operation(summary = "Update an existing skill", description = "Admin cập nhật kỹ năng hiện có.")
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

    /**
     * Admin xoá kỹ năng hiện có.
     * @param skillId ID của kỹ năng cần xoá.
     * @return ResponseData chứa mã trạng thái và thông báo xoá kỹ năng.
     */
    @Operation(summary = "Delete an existing skill", description = "Admin xoá kỹ năng hiện có.")
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

    /**
     * Lấy thông tin chi tiết kỹ năng theo ID.
     * @param skillId ID của kỹ năng cần lấy.
     * @return ResponseData chứa mã trạng thái và thông tin kỹ năng.
     */
    @Operation(summary = "Get skill by ID", description = "Lấy thông tin chi tiết kỹ năng theo ID.")
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

    /**
     * Tìm kiếm kỹ năng theo từ khoá.
     * @param keyword Từ khoá tìm kiếm.
     * @return ResponseData chứa mã trạng thái và danh sách kỹ năng khớp với từ khoá.
     */
    @Operation(summary = "Search skills by keyword", description = "Tìm kiếm kỹ năng theo từ khoá.")
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

    /**
     * Lấy danh sách tất cả kỹ năng.
     * @return ResponseData chứa mã trạng thái và danh sách tất cả kỹ năng.
     */
    @Operation(summary = "Get all skills", description = "Lấy danh sách tất cả kỹ năng.")
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

    /**
     * Lấy danh sách kỹ năng với phân trang.
     * @param page Số trang.
     * @param size Kích thước trang.
     * @return ResponseData chứa mã trạng thái và danh sách kỹ năng theo trang.
     */
    @Operation(summary = "Get all skills with pagination", description = "Lấy danh sách kỹ năng với phân trang.")
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
