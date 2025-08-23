package com.demoJob.demo.repository;

import com.demoJob.demo.entity.Job;
import com.demoJob.demo.util.enums.JobStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findByIdAndStatus(Long jobId, JobStatus status);

    /**
     * Query lấy ra các Job đã active cho User
     *
     * @param status Trạng thái Job
     * @param pageable Phân trang
     * @return trả dữ liệu về dạng phân trang
     */
    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    /**
     * Query tìm kiếm ra list job theo skill name
     */
    @Query("""
            SELECT DISTINCT j
            FROM Job j JOIN j.skills s
            WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :skillName, '%'))
            """)
    List<Job> findBySkillName(@Param("skillName") String skillName);
}