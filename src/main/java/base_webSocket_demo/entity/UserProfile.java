package base_webSocket_demo.entity;

import base_webSocket_demo.util.Gender;
import base_webSocket_demo.util.validator.PhoneNumber;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_user_profile")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfile extends AbstractEntity<Long> {

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    @PhoneNumber
    private String phone;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String summary;

    private String website;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
