package com.dalhousie.FundFusion.authentication.service;

import com.dalhousie.FundFusion.authentication.requestEntity.AuthenticateRequest;
import com.dalhousie.FundFusion.authentication.requestEntity.OtpVarificationRequest;
import com.dalhousie.FundFusion.authentication.requestEntity.RegisterRequest;
import com.dalhousie.FundFusion.authentication.responseEntity.AuthenticationResponse;
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