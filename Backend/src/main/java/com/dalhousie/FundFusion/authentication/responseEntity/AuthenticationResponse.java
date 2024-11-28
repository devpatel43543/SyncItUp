package com.dalhousie.FundFusion.authentication.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;

    /**
     * Validates the authentication response fields.
     *
     * @throws IllegalArgumentException if the token is invalid.
     */
    public void validate() {
        if (isInvalidToken(token)) {
            throw new IllegalArgumentException("Token is required and cannot be empty.");
        }
    }

    private boolean isInvalidToken(String token) {
        return token == null || token.isBlank();
    }
}
