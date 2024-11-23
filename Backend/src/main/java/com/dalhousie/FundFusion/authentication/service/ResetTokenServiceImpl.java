package com.dalhousie.FundFusion.authentication.service;

import com.dalhousie.FundFusion.exception.TokenExpiredException;
import com.dalhousie.FundFusion.authentication.entity.PasswordReset;
import com.dalhousie.FundFusion.authentication.repository.PasswordResetTokenRepository;
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
    private final long EXPIRATION_DURATION = 1000L * 60 * 10;
    @Override
    public PasswordReset createResetPasswordToken(Integer userId) {

        // If the user already has a reset token, delete it before creating a new one
        passwordResetTokenRepository.findByUserId(userId).ifPresent(passwordResetTokenRepository::delete);

        String token = generateToken();
        Instant expiryDate = calculateExpiryDate();

        PasswordReset passwordReset = buildPasswordReset(userId, token, expiryDate);

        log.info("Generated Password Reset Token: {}", passwordReset);

        return passwordResetTokenRepository.save(passwordReset);
    }

    // Helper method to generate a token
    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    // Helper method to calculate the expiry date
    private Instant calculateExpiryDate() {
        return Instant.now().plusMillis(EXPIRATION_DURATION);
    }

    // Helper method to build PasswordReset object
    private PasswordReset buildPasswordReset(Integer userId, String token, Instant expiryDate) {
        return PasswordReset.builder()
                .userId(userId)
                .token(token)
                .expiryDate(expiryDate)
                .build();
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
