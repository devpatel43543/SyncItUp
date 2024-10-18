package com.dalhousie.FundFusion.user.service.userAuthenticationService;

import com.dalhousie.FundFusion.user.requestEntity.AuthenticateRequest;
import com.dalhousie.FundFusion.user.requestEntity.RegisterRequest;
import com.dalhousie.FundFusion.user.responseEntity.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    AuthenticationResponse registerUser(RegisterRequest registerRequest);

    AuthenticationResponse authenticateUser(AuthenticateRequest authenticateRequest);
    String getURL(HttpServletRequest request);
    void forgotPassword(String email, String resetUrl);
    void resetPassword(String email, String password, String token);
}