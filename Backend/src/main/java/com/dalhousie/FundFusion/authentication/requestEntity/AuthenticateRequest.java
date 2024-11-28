package com.dalhousie.FundFusion.authentication.requestEntity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticateRequest {
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
}
