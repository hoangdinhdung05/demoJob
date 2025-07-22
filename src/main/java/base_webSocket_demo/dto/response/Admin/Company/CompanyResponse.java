package base_webSocket_demo.dto.response.Admin.Company;

import base_webSocket_demo.util.CompanyStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyResponse {

    private Long id;

    private String name;

    private String logo;

    private String email;

    private String phone;

    private String website;

    private CompanyStatus status;

    private CompanyProfileResponse companyProfile;

    private String createdBy;

    private String updatedBy;

}
