package com.dalhousie.FundFusion.user.service.passwordResetService;

import com.dalhousie.FundFusion.user.entity.PasswordReset;

import java.util.Optional;

public interface ResetTokenService {
    PasswordReset createResetPasswordToken(Integer userId);
    Optional<PasswordReset> findByUserId(Integer userId);

    void deleteResetPasswordToken(PasswordReset resetPasswordToken);
    boolean isTokenValid(PasswordReset token);

}
