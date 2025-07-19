package base_webSocket_demo.dto;

import java.util.Set;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExportDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private Set<String> roles;

    public String getRoleNames() {
        if (roles == null || roles.isEmpty()) return "";
        return String.join(", ", roles);
    }
}
