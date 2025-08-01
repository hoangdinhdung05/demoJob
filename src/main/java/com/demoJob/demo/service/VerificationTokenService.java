package com.demoJob.demo.service;

import com.demoJob.demo.entity.User;
import com.demoJob.demo.entity.VerificationToken;
import com.demoJob.demo.util.TokenTypeVerify;

public interface VerificationTokenService {
    VerificationToken createToken(User user, TokenTypeVerify type, int minutesValid);
    VerificationToken validateToken(String tokenStr, TokenTypeVerify expectedType);
}
