package com.dalhousie.FundFusion.authentication.service;

import com.dalhousie.FundFusion.authentication.requestEntity.AuthenticateRequest;
import com.dalhousie.FundFusion.authentication.requestEntity.OtpVarificationRequest;
import com.dalhousie.FundFusion.authentication.requestEntity.RegisterRequest;
import com.dalhousie.FundFusion.authentication.requestEntity.ResetPasswordRequest;
import com.dalhousie.FundFusion.authentication.responseEntity.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    AuthenticationResponse registerUser(RegisterRequest registerRequest);

    AuthenticationResponse authenticateUser(AuthenticateRequest authenticateRequest);
      void resetPassword(ResetPasswordRequest resetPasswordRequest);
    AuthenticationResponse verifyOtp(OtpVarificationRequest otpVarificationRequest);
    void resendOtp();
    void handleForgotPassword(HttpServletRequest servletRequest, String email);
    }