package base_webSocket_demo.repository;

import base_webSocket_demo.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByUserIdAndJobId(Long userId, Long jobId);
    List<Resume> findByUserId(Long userId);
    List<Resume> findByJobId(Long jobId);
    boolean existsByUserIdAndJobId(Long userId, Long jobId);
}
