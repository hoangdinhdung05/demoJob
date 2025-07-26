package base_webSocket_demo.entity;

import base_webSocket_demo.util.TokenTypeVerify;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_verification_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken extends AbstractEntity<Long> {
    @Column(nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenTypeVerify type; // RESET_PASSWORD, VERIFY_ACCOUNT

    private boolean used;

    private LocalDateTime expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}
