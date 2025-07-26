package base_webSocket_demo.service;

import base_webSocket_demo.entity.User;
import base_webSocket_demo.entity.VerificationToken;
import base_webSocket_demo.util.TokenTypeVerify;

public interface VerificationTokenService {
    VerificationToken createToken(User user, TokenTypeVerify type, int minutesValid);
    VerificationToken validateToken(String tokenStr, TokenTypeVerify expectedType);
}
