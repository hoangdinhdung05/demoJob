package base_webSocket_demo.entity;

import base_webSocket_demo.util.CompanyStatus;
import base_webSocket_demo.util.validator.PhoneNumber;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_company")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Company extends AbstractEntity<Long> {

    private String name;

    private String logo;

    private String email;

    @PhoneNumber
    private String phone;

    private String website;

    @Enumerated(EnumType.STRING)
    private CompanyStatus status;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CompanyProfile profile;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserCompany> userCompanies = new HashSet<>();


}
