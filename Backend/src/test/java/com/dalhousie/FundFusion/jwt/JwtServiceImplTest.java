package com.dalhousie.FundFusion.jwt;

import com.dalhousie.FundFusion.jwt.JwtServiceImpl;
import com.dalhousie.FundFusion.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtServiceImpl();
    }

    @Test
    void testExtractUsername_ValidToken() {
        UserDetails userDetails = User.builder()
                .id(1)
                .name("Test User")
                .email("testuser@example.com")
                .password("password")
                .isEmailVerified(true)
                .build();

        String token = jwtService.generateToken(userDetails, true);

        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        String username = jwtService.extractUsername(token);

        assertNotNull(username);
        assertEquals("testuser@example.com", username);
    }

    @Test
    void testExtractUsername_InvalidToken() {
        String token = "invalidToken";

        String username = jwtService.extractUsername(token);

        assertNull(username);
    }

    @Test
    void testIsEmailVerified_ValidToken() {
        String token = jwtService.generateToken(userDetails, true);
        boolean emailVerified = jwtService.isEmailVerified(token);

        assertTrue(emailVerified);
    }

    @Test
    void testIsEmailVerified_InvalidToken() {
        String token = "invalidToken";

        boolean emailVerified = jwtService.isEmailVerified(token);

        assertFalse(emailVerified);
    }

    @Test
    void testGenerateToken_ValidUserDetails() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(userDetails, true);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenValid_ValidToken() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(userDetails, true);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenValid_InvalidToken() {
        String token = "invalidToken";

        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testBuildToken() {
        when(userDetails.getUsername()).thenReturn("testuser");

        Map<String, Object> claims = Map.of("emailVerified", true);
        String token = jwtService.generateToken(userDetails, true);

        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
        assertTrue(jwtService.isEmailVerified(token));
    }
}

