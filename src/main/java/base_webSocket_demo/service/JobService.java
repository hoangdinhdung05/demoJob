package base_webSocket_demo.service;

import base_webSocket_demo.dto.request.Admin.Job.JobRequest;
import base_webSocket_demo.dto.response.Admin.Job.JobResponse;
import base_webSocket_demo.dto.response.system.PageResponse;
import base_webSocket_demo.util.JobStatus;
import java.util.List;

public interface JobService {

    JobResponse createJob(JobRequest request, long userId);

    JobResponse updateJob(long jobId, JobRequest request);

    void deleteJob(long jobId);

    JobResponse changJobStatus(long jobId, JobStatus jobStatus);

    JobResponse getById(Long id);

    List<JobResponse> getByCompanyId(Long companyId);

    List<JobResponse> getBySkillId(Long skillId);

    List<JobResponse> searchByName(String keyword);

    List<JobResponse> getAlls();

    PageResponse<?> getAllPage(int page, int size);

}
