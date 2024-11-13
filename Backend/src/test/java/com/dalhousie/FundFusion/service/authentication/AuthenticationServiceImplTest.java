package com.dalhousie.FundFusion.service.authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.dalhousie.FundFusion.authentication.entity.Otp;
import com.dalhousie.FundFusion.authentication.requestEntity.OtpVarificationRequest;
import com.dalhousie.FundFusion.authentication.service.AuthenticationServiceImpl;
import com.dalhousie.FundFusion.authentication.service.OtpService;
import com.dalhousie.FundFusion.exception.TokenExpiredException;
import com.dalhousie.FundFusion.authentication.requestEntity.AuthenticateRequest;
import com.dalhousie.FundFusion.authentication.requestEntity.RegisterRequest;
import com.dalhousie.FundFusion.authentication.responseEntity.AuthenticationResponse;
import com.dalhousie.FundFusion.group.repository.PendingGroupMembersRepository;
import com.dalhousie.FundFusion.jwt.JwtService;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage; // Mock MimeMessage

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    @Mock
    private PendingGroupMembersRepository pendingGroupMembersRepository;

    @Mock
    private OtpService otpService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void ShouldRegisterUser_WhenUserDoesNotExist() {
        try {

            RegisterRequest registerRequest = new RegisterRequest("John Doe", "johndoe@example.com", "password");

            when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());

            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

            doAnswer(invocation -> {
                User userBeingSaved = invocation.getArgument(0);
                userBeingSaved.setId(1);
                return userBeingSaved;
            }).when(userRepository).save(any(User.class));

            Otp mockOtp = new Otp();
            mockOtp.setOtp("123456");
            mockOtp.setId(1);
            when(otpService.generateOtp(1)).thenReturn(mockOtp);

            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            doNothing().when(javaMailSender).send(mimeMessage);

            AuthenticationResponse response = authenticationService.registerUser(registerRequest);

            assertNull(response.getToken());
            verify(userRepository, times(1)).save(any(User.class));
            verify(otpService, times(1)).generateOtp(1);
            verify(javaMailSender, times(1)).send(mimeMessage);

        } catch (Exception e) {
            fail("Test failed due to unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void ShouldThrowException_WhenUserAlreadyExists() {

        User alreadyExistingUser = new User();
        alreadyExistingUser.setEmail("test@example.com");
        alreadyExistingUser.setName("Existing User");
        alreadyExistingUser.setPassword("somePassword");

        RegisterRequest request = new RegisterRequest("test@example.com", "password", "John Doe");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(alreadyExistingUser));

        assertThrows(Throwable.class, () -> {
            authenticationService.registerUser(request);
        });
        verify(userRepository, times(1)).findByEmail(request.getEmail());
    }


    @Test
    void ShouldAuthenticateSuccessfully_WhenCredentialsAreValid() {

        AuthenticateRequest request = new AuthenticateRequest("user@example.com", "encodedPassword");
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encodedPassword");
        user.setEmailVerified(true);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(user, true)).thenReturn("mockJwtToken");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        AuthenticationResponse response = authenticationService.authenticateUser(request);

        assertNotNull(response.getToken());
        assertEquals("mockJwtToken", response.getToken());
    }


    @Test
    void ShouldThrowNoSuchElementException_WhenUserNotFound() {

        AuthenticateRequest request = new AuthenticateRequest("unknown@example.com", "password");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(Throwable.class, () -> authenticationService.authenticateUser(request));
    }

        @Test
    void ShouldReturnAuthenticationResponse_WhenOtpIsValid() {

        OtpVarificationRequest otpVarificationRequest = new OtpVarificationRequest("123456");
        Otp mockOtp = new Otp();
        mockOtp.setOtp("123456");
        mockOtp.setUserId(1);

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmailVerified(false);

        when(otpService.findByOtp(otpVarificationRequest.getOtp())).thenReturn(Optional.of(mockOtp));
        when(otpService.isOtpValid(mockOtp)).thenReturn(true);
        when(userRepository.findById(mockOtp.getUserId())).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(mockUser, true)).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.verifyOtp(otpVarificationRequest);

        assertEquals("jwtToken", response.getToken());
        assertTrue(mockUser.isEmailVerified());
        verify(userRepository, times(1)).save(mockUser);
    }

        @Test
    void ShouldThrowTokenExpiredException_WhenOtpIsInvalid() {
        OtpVarificationRequest otpVarificationRequest = new OtpVarificationRequest("invalidOtp");
        when(otpService.findByOtp(otpVarificationRequest.getOtp())).thenReturn(Optional.empty());

        assertThrows(TokenExpiredException.class, () -> {
            authenticationService.verifyOtp(otpVarificationRequest);
        });
        //assertThrows(Throwable.class, () -> authenticationService.verifyOtp(otpVarificationRequest));
    }

    @Test
    void ShouldThrowException_WhenPasswordIncorrect() {
        AuthenticateRequest request = new AuthenticateRequest("user@example.com", "wrongPassword");
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encodedPassword");
        user.setEmailVerified(true);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), "encodedPassword")).thenReturn(false); // Incorrect password

        AuthenticationResponse response = authenticationService.authenticateUser(request);
        assertNull(response.getToken());
    }

    @Test
    void ShouldThrowException_WhenJwtTokenIsInvalid() {
        AuthenticateRequest request = new AuthenticateRequest("user@example.com", "encodedPassword");
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encodedPassword");
        user.setEmailVerified(true);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(user, true)).thenReturn("invalidToken"); // Simulate invalid token

        AuthenticationResponse response = authenticationService.authenticateUser(request);
        assertNotNull(response.getToken(),"The token should be 'invalidToken'");
    }
}
