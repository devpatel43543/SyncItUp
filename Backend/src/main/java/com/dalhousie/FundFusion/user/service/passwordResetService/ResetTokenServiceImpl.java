package com.dalhousie.FundFusion.user.service.passwordResetService;

import com.dalhousie.FundFusion.exception.TokenExpiredException;
import com.dalhousie.FundFusion.user.entity.PasswordReset;
import com.dalhousie.FundFusion.user.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResetTokenServiceImpl implements ResetTokenService{
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final long EXPIRATION_DURATION_MS = 1000L * 60 * 5;
    @Override
    public PasswordReset createResetPasswordToken(Integer userId) {

        // If the user already has a reset token, delete it before creating a new one
        passwordResetTokenRepository.findByUserId(userId).ifPresent(passwordResetTokenRepository::delete);

        // Create a new reset token with a random UUID and set the expiry date
        PasswordReset passwordReset = PasswordReset.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString()) // Generate random token
                .expiryDate(Instant.now().plusMillis(EXPIRATION_DURATION_MS)) // Set expiry 10 minutes from now
                .build();
        log.info(String.valueOf(passwordReset));
        // Save the token in the repository and return
        return passwordResetTokenRepository.save(passwordReset);
    }
    @Override
    public Optional<PasswordReset> findByUserId(Integer userId) {
        return passwordResetTokenRepository.findByUserId(userId);
    }
    @Override
    public void deleteResetPasswordToken(PasswordReset resetPasswordToken) {
        if (resetPasswordToken != null) {
            passwordResetTokenRepository.delete(resetPasswordToken);
        }
    }
    @Override
    public boolean isTokenValid(PasswordReset token) {
        // Check if the token has expired
        if (token == null || token.getExpiryDate() == null) {
            return false;
        }
        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new TokenExpiredException("token has expired.");
        }

        return true;
    }
}
