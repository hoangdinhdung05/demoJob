package base_webSocket_demo.entity;

import base_webSocket_demo.util.CompanyStatus;
import jakarta.persistence.*;
import lombok.*;

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

    private String phone;

    private String website;

    @Enumerated(EnumType.STRING)
    private CompanyStatus status;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CompanyProfile profile;

}
