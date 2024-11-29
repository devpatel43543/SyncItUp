package com.dalhousie.FundFusion.authentication.requestEntity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    /**
     * Validates the forgot password request fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (isInvalidEmail(email)) {
            throw new IllegalArgumentException("Invalid email address.");
        }
    }

    private boolean isInvalidEmail(String email) {
        return isNullOrBlank(email) || isInvalidFormat(email);
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isInvalidFormat(String email) {
        return !email.contains("@");
    }
}
