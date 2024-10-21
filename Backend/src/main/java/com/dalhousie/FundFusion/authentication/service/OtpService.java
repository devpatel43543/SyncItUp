package com.dalhousie.FundFusion.authentication.service;

import com.dalhousie.FundFusion.authentication.entity.Otp;

import java.util.Optional;

public interface OtpService {
    Otp generateOtp(Integer user);
    boolean isOtpValid(Otp otp);
    void deleteOtp(Otp otp);
    Optional<Otp> findByOtp(String otpValue);
    Otp resendOtp(Integer userId);
}
