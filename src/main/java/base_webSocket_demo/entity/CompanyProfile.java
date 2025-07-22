package base_webSocket_demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_company_profile")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompanyProfile extends AbstractEntity<Long> {

    @OneToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String address;

    private String city;

    private String country;

    private String industry;

    private String taxCode;

    private String companySize;

    private String workingTime;

    @Column(columnDefinition = "TEXT")
    private String culture;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    private String mapLocation;
}
