package com.demoJob.demo.repository;

import com.demoJob.demo.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    /**
     * Query kiểm tra resume exists thông qua userId và jobId
     */
    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    /**
     * QUery lấy ra resume theo created_by
     * User sử dụng xem resume đã create
     */
    Page<Resume> findByCreatedBy(String createdBy, Pageable pageable);

    /**
     * Query lấy ra resume theo companyId
     */
    Page<Resume> findByJob_Company_IdIn(List<Long> companyIds, Pageable pageable);
}
