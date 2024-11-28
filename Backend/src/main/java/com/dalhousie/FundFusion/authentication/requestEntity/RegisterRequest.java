package com.dalhousie.FundFusion.authentication.requestEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    private static final int MIN_PASSWORD_LENGTH = 8;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;


    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must have at least 8 characters")
    private String password;

    /**
     * Validates the register request fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (isInvalidName(name)) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (isInvalidEmail(email)) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        if (isInvalidPassword(password)) {
            throw new IllegalArgumentException("Password must have at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
    }

    private boolean isInvalidName(String name) {
        return isNullOrBlank(name);
    }

    private boolean isInvalidEmail(String email) {
        return isNullOrBlank(email) || isInvalidFormat(email);
    }

    private boolean isInvalidPassword(String password) {
        return password == null || password.length() < MIN_PASSWORD_LENGTH;
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isInvalidFormat(String email) {
        return !email.contains("@");
    }
}
