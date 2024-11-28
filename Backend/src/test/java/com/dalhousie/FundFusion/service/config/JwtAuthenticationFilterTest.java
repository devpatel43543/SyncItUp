package com.dalhousie.FundFusion.service.config;

import com.dalhousie.FundFusion.config.JwtAuthenticationFilter;
import com.dalhousie.FundFusion.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_ExcludedUrl() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/check/some-url");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void testDoFilterInternal_NoAuthHeader() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void testDoFilterInternal_InvalidAuthHeader() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void testDoFilterInternal_InvalidJwtToken() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");

        doThrow(new RuntimeException("Invalid token")).when(jwtService).extractUsername("invalidToken");

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                jwtAuthenticationFilter.doFilterInternal(request, response, filterChain)
        );

        assertEquals("Invalid token", exception.getMessage());

        verifyNoInteractions(userDetailsService);
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testDoFilterInternal_ExpiredJwtToken() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");

        doThrow(new RuntimeException("Token expired")).when(jwtService).extractUsername("expiredToken");

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                jwtAuthenticationFilter.doFilterInternal(request, response, filterChain)
        );

        assertEquals("Token expired", exception.getMessage());

        verifyNoInteractions(userDetailsService);
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testDoFilterInternal_EmailNotVerified() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtService.extractUsername("validToken")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("validToken", userDetails)).thenReturn(true);
        when(jwtService.isEmailVerified("validToken")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN, "Email not verified.");
        verifyNoInteractions(filterChain);
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtService.extractUsername("validToken")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("validToken", userDetails)).thenReturn(true);
        when(jwtService.isEmailVerified("validToken")).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_UserAlreadyAuthenticated() throws ServletException, IOException {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}
