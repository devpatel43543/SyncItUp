package com.dalhousie.FundFusion.authentication.requestEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticateRequest {
    private String email;
    private String password;

    /**
     * Validates the authentication request fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (isInvalidEmail(email)) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        if (isInvalidPassword(password)) {
            throw new IllegalArgumentException("Password cannot be empty.");
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

    private boolean isInvalidPassword(String password) {
        return isNullOrBlank(password);
    }
}