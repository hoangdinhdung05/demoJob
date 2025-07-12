package base_webSocket_demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRequest {

    private String userName;
    private String passWord;

}
