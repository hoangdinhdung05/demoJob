package base_webSocket_demo.repository;

import base_webSocket_demo.entity.UserCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCompanyRepository extends JpaRepository<UserCompany, Long> {
    boolean findByUserId(long userId);
    boolean findByCompanyId(long companyId);
    void deleteByCompanyId(Long companyId);
}
