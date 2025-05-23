package com.dalhousie.FundFusion.service.authentication;

import com.dalhousie.FundFusion.authentication.service.OtpServiceImpl;
import com.dalhousie.FundFusion.exception.TokenExpiredException;
import com.dalhousie.FundFusion.authentication.entity.Otp;
import com.dalhousie.FundFusion.authentication.repository.OtpRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OtpServiceImplTest {

    @Mock
    private OtpRepository otpRepository;

    @InjectMocks
    private OtpServiceImpl otpService;

    private Otp otp;

    private static final long MS = 1000L;
    private static final int SEC = 60;
    private static final int MIN = 10;
    private static final int OTP_LENGTH = 6;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        otp = Otp.builder()
                .otp("123456")
                .expiryDate(Instant.now().plusMillis(MS * SEC * MIN))
                .userId(1)
                .build();
    }

    @Test
    public void testGenerateOtp() {

        Integer userId = 1;
        when(otpRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(otpRepository.save(any(Otp.class))).thenReturn(otp);

        Otp generatedOtp = otpService.generateOtp(userId);

        assertNotNull(generatedOtp);
        assertEquals(userId, generatedOtp.getUserId());
        assertEquals(OTP_LENGTH, generatedOtp.getOtp().length());
        verify(otpRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void testResendOtp() {

        Integer userId = 1;
        Otp existingOtp = Otp.builder()
                .otp("123456")
                .expiryDate(Instant.now().plusMillis(MS * SEC * MIN))
                .userId(userId)
                .build();
        Otp newOtp = Otp.builder()
                .otp("654321") // This should be a new OTP value
                .expiryDate(Instant.now().plusMillis(MS * SEC * MIN))
                .userId(userId)
                .build();

        when(otpRepository.findByUserId(userId)).thenReturn(Optional.of(existingOtp));
        when(otpRepository.save(any(Otp.class))).thenReturn(newOtp);
        Otp generatedOtp = otpService.resendOtp(userId);

        assertNotNull(generatedOtp);
        assertNotEquals(existingOtp.getOtp(), generatedOtp.getOtp());
    }

    @Test
    public void testDeleteOtp() {

        Integer userId = 1;
        when(otpRepository.findByUserId(userId)).thenReturn(Optional.of(otp));

        otpService.deleteOtp(otp);
        verify(otpRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    public void testFindByOtp() {

        String otpValue = "123456";
        when(otpRepository.findByOtp(otpValue)).thenReturn(Optional.of(otp));

        Optional<Otp> foundOtp = otpService.findByOtp(otpValue);

        assertTrue(foundOtp.isPresent());
        assertEquals(otpValue, foundOtp.get().getOtp());
        verify(otpRepository, times(1)).findByOtp(otpValue);
    }

    @Test
    public void testIsOtpValid_validOtp() {

        boolean valid = otpService.isOtpValid(otp);
        assertTrue(valid);
    }

    @Test
    public void testIsOtpValid_expiredOtp() {

        otp.setExpiryDate(Instant.now().minusMillis(MS * SEC * MIN));
        TokenExpiredException exception = assertThrows(TokenExpiredException.class, () -> otpService.isOtpValid(otp));
        assertEquals("otp has expired.", exception.getMessage());
    }

    @Test
    public void testIsOtpValid_nullOtp() {
        assertFalse(otpService.isOtpValid(null));
    }

    @Test
    public void testIsOtpValid_nullExpiryDate() {
        otp.setExpiryDate(null);
        assertFalse(otpService.isOtpValid(otp));
    }
}
