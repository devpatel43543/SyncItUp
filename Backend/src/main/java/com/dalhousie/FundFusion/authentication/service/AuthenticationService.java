package com.dalhousie.FundFusion.authentication.service;

import com.dalhousie.FundFusion.user.requestEntity.AuthenticateRequest;
import com.dalhousie.FundFusion.user.requestEntity.OtpVarificationRequest;
import com.dalhousie.FundFusion.user.requestEntity.RegisterRequest;
import com.dalhousie.FundFusion.user.responseEntity.AuthenticationResponse;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    AuthenticationResponse registerUser(RegisterRequest registerRequest);

    AuthenticationResponse authenticateUser(AuthenticateRequest authenticateRequest);
    String getURL(HttpServletRequest request);
    void forgotPassword(String email, String resetUrl);
    void resetPassword(String email, String password, String token);
    AuthenticationResponse verifyOtp(OtpVarificationRequest otpVarificationRequest);
    void resendOtp();
    }