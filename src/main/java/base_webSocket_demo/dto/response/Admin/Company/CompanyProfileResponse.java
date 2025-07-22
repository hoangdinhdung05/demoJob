package base_webSocket_demo.dto.response.Admin.Company;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyProfileResponse {

    private String description;

    private String address;

    private String city;

    private String country;

    private String industry;

    private String taxCode;

    private String companySize;

    private String workingTime;

    private String culture;

    private String benefits;

    private String mapLocation;

}
