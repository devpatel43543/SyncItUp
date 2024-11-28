package com.dalhousie.FundFusion.authentication.requestEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {
    private static final int MIN_PASSWORD_LENGTH = 8;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = MIN_PASSWORD_LENGTH, message = "Password must have at least " + MIN_PASSWORD_LENGTH + " characters!")
    private String password;

    @NotBlank(message = "Token is required")
    private String token;

    /**
     * Validates the reset password request fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (isInvalidEmail(email)) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        if (isInvalidPassword(password)) {
            throw new IllegalArgumentException("Password must have at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (isInvalidToken(token)) {
            throw new IllegalArgumentException("Token is required.");
        }
    }

    private boolean isInvalidEmail(String email) {
        return isNullOrBlank(email) || isInvalidFormat(email);
    }

    private boolean isInvalidPassword(String password) {
        return password == null || password.length() < MIN_PASSWORD_LENGTH;
    }

    private boolean isInvalidToken(String token) {
        return isNullOrBlank(token);
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isInvalidFormat(String email) {
        return !email.contains("@");
    }
}
