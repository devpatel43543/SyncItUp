package com.dalhousie.FundFusion.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService{
    private final static String SECRET_KEY = "C018A285FC4D3473B67F1D29E40C40FEACF3F9B4F01399D9421E562F01CC6B05";

    @Override
    public String extractUsername(String jwtToken) {
        try {
            return extractClaim(jwtToken, Claims::getSubject);
        } catch (Exception exception) {
            log.error("Exception occurred while extracting user name: {}", exception.getMessage());
            return null;
        }
    }


    @Override
    public String generateToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        boolean isTokenExpired = isTokenExpired(token);
        final String username = extractUsername(token);
        return (Objects.equals(username, userDetails.getUsername())) && !isTokenExpired;

    }

    private boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (Exception exception) {
            log.error("Exception occurred while validating token: {}", exception.getMessage());
            return false;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] KeyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(KeyBytes);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // For now setting token expiration to 10 days
                // [TODO]: Setup refresh token concept for updating expired tokens
                .setExpiration(new Date(
                        System.currentTimeMillis() +
                                1000L/*Milli seconds*/ *
                                        60/*Seconds*/ *
                                        60/*Minutes*/ *
                                        24/*Hours*/ *
                                        10/*Days*/
                ))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}