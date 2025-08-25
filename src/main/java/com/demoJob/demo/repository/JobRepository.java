package com.demoJob.demo.repository;

import com.demoJob.demo.entity.Job;
import com.demoJob.demo.util.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    /**
     * Query lấy ra các Job đã active cho User
     *
     * @param status Trạng thái Job
     * @param pageable Phân trang
     * @return trả dữ liệu về dạng phân trang
     */
    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    /**
     * Query lấy các Job theo CompanyId
     * @param companyId companyId
     * @param pageable Phân trang
     * @return trả về list job
     */
    Page<Job> findByCompanyId(Long companyId, Pageable pageable);

}