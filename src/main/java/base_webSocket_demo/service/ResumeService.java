package base_webSocket_demo.service;

import base_webSocket_demo.dto.request.Admin.Resume.ResumeRequest;
import base_webSocket_demo.dto.response.Admin.Resume.ResumeCreateResponse;
import base_webSocket_demo.dto.response.Admin.Resume.ResumeResponse;
import base_webSocket_demo.dto.response.Admin.Resume.ResumeUpdateResponse;
import base_webSocket_demo.dto.response.system.PageResponse;
import base_webSocket_demo.entity.Resume;
import java.util.List;

public interface ResumeService {

    ResumeCreateResponse createResume(ResumeRequest request);

    ResumeUpdateResponse updateResume(ResumeRequest request);

    void deleteResume(long resumeId);

    ResumeResponse getResumeById(long resumeId);

    List<ResumeResponse> getResumeByUserId(long userId);

    List<ResumeResponse> getResumeByJobId(long jobId);

    List<ResumeResponse> getList();

    PageResponse<?> getPageResume(int page, int size);

    boolean checkResumeExistsByUserAndJob(Resume resume);

}
