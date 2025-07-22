package base_webSocket_demo.dto.request.Admin.Company;

import base_webSocket_demo.util.CompanyStatus;
import base_webSocket_demo.util.validator.PhoneNumber;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CompanyRequest {

    @NotNull(message = "Name company not null")
    private String name;

    private String logo;

    @NotNull(message = "Company email not null")
    private String email;

    @PhoneNumber
    private String phone;

    private String website;

    private CompanyStatus status;

    private CompanyProfileRequest companyProfileRequest;

}
