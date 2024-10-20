package com.dalhousie.FundFusion.jwt;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String jwtToken);

    String generateToken(UserDetails userDetails,boolean isEmailVerified);
    boolean isEmailVerified(String jwtToken);
    boolean isTokenValid(String token, UserDetails userDetails);
}