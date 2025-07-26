package base_webSocket_demo.repository;

import base_webSocket_demo.entity.OtpCode;
import base_webSocket_demo.util.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByUserEmailAndCodeAndTypeAndUsedIsFalse(String email, String code, OtpType type);
}
