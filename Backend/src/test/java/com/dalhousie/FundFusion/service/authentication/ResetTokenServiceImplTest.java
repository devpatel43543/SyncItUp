package com.dalhousie.FundFusion.service.authentication;

import com.dalhousie.FundFusion.authentication.entity.PasswordReset;
import com.dalhousie.FundFusion.authentication.repository.PasswordResetTokenRepository;
import com.dalhousie.FundFusion.authentication.service.ResetTokenServiceImpl;
import com.dalhousie.FundFusion.exception.TokenExpiredException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResetTokenServiceImplTest {

    @InjectMocks
    private ResetTokenServiceImpl resetTokenService;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private static final int USER_ID = 1;
    private static final String TOKEN = UUID.randomUUID().toString();
    private static final Instant EXPIRY_DATE = Instant.now().plusMillis(1000L * 60 * 10);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateResetPasswordToken_NewToken() {

        PasswordReset newPasswordReset = PasswordReset.builder()
                .userId(USER_ID)
                .token(TOKEN)
                .expiryDate(EXPIRY_DATE)
                .build();


        when(passwordResetTokenRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(passwordResetTokenRepository.save(any(PasswordReset.class))).thenReturn(newPasswordReset);


        PasswordReset createdToken = resetTokenService.createResetPasswordToken(USER_ID);


        assertNotNull(createdToken);
        assertEquals(USER_ID, createdToken.getUserId());
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordReset.class));
    }

    @Test
    void testCreateResetPasswordToken_ExistingToken() {

        PasswordReset existingPasswordReset = PasswordReset.builder()
                .userId(USER_ID)
                .token("existingToken")
                .expiryDate(Instant.now().plusMillis(1000L * 60 * 10))
                .build();
        PasswordReset newPasswordReset = PasswordReset.builder()
                .userId(USER_ID)
                .token(TOKEN)
                .expiryDate(EXPIRY_DATE)
                .build();

        when(passwordResetTokenRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingPasswordReset));
        when(passwordResetTokenRepository.save(any(PasswordReset.class))).thenReturn(newPasswordReset);


        PasswordReset createdToken = resetTokenService.createResetPasswordToken(USER_ID);


        assertNotNull(createdToken);
        assertEquals(USER_ID, createdToken.getUserId());
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordReset.class));
    }

    @Test
    void testFindByUserId() {

        PasswordReset passwordReset = PasswordReset.builder()
                .userId(USER_ID)
                .token(TOKEN)
                .expiryDate(EXPIRY_DATE)
                .build();

        when(passwordResetTokenRepository.findByUserId(USER_ID)).thenReturn(Optional.of(passwordReset));
        Optional<PasswordReset> foundToken = resetTokenService.findByUserId(USER_ID);

        assertTrue(foundToken.isPresent());
        assertEquals(USER_ID, foundToken.get().getUserId());
    }

    @Test
    void testFindByUserId_NotFound() {

        when(passwordResetTokenRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        Optional<PasswordReset> foundToken = resetTokenService.findByUserId(USER_ID);
        assertFalse(foundToken.isPresent());
    }

    @Test
    void testDeleteResetPasswordToken() {

        PasswordReset passwordReset = PasswordReset.builder()
                .userId(USER_ID)
                .token(TOKEN)
                .expiryDate(EXPIRY_DATE)
                .build();

        resetTokenService.deleteResetPasswordToken(passwordReset);
        verify(passwordResetTokenRepository, times(1)).delete(passwordReset);
    }

    @Test
    void testDeleteResetPasswordToken_Null() {

        resetTokenService.deleteResetPasswordToken(null);
        verify(passwordResetTokenRepository, times(0)).delete(any());
    }

    @Test
    void testIsTokenValid_ValidToken() {

        PasswordReset passwordReset = PasswordReset.builder()
                .userId(USER_ID)
                .token(TOKEN)
                .expiryDate(EXPIRY_DATE)
                .build();


        boolean isValid = resetTokenService.isTokenValid(passwordReset);
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_ExpiredToken() {

        PasswordReset expiredPasswordReset = PasswordReset.builder()
                .userId(USER_ID)
                .token(TOKEN)
                .expiryDate(Instant.now().minusMillis(1000L * 60 * 10))
                .build();


        TokenExpiredException exception = assertThrows(TokenExpiredException.class, () -> {
            resetTokenService.isTokenValid(expiredPasswordReset);
        });
        assertEquals("token has expired.", exception.getMessage());
    }

    @Test
    void testIsTokenValid_NullToken() {

        boolean isValid = resetTokenService.isTokenValid(null);
        assertFalse(isValid);
    }
}

