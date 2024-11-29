package com.dalhousie.FundFusion.controller.authentication;

import com.dalhousie.FundFusion.authentication.controller.AuthenticationController;
import com.dalhousie.FundFusion.authentication.entity.PasswordReset;
import com.dalhousie.FundFusion.authentication.repository.PasswordResetTokenRepository;
import com.dalhousie.FundFusion.authentication.requestEntity.*;
import com.dalhousie.FundFusion.authentication.responseEntity.AuthenticationResponse;
import com.dalhousie.FundFusion.authentication.service.AuthenticationService;
import com.dalhousie.FundFusion.authentication.service.ResetTokenService;
import com.dalhousie.FundFusion.exception.TokenExpiredException;
import com.dalhousie.FundFusion.exception.UserNotFoundException;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.repository.UserRepository;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    @Test
    void testRegister_Success() throws Exception {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);

        AuthenticationResponse authResponse = new AuthenticationResponse("testToken");

        when(mockService.registerUser(Mockito.any(RegisterRequest.class)))
                .thenReturn(authResponse);

        AuthenticationController controller = new AuthenticationController(mockService);

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        ResponseEntity<CustomResponseBody<AuthenticationResponse>> response =
                controller.register(registerRequest);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("User registered successfully, verify email", response.getBody().message());
        Assertions.assertEquals("testToken", response.getBody().data().getToken());
    }

    @Test
    void testRegister_Failure_UnexpectedError() throws Exception {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);
        Mockito.doThrow(new RuntimeException("Unexpected Error"))
                .when(mockService)
                .registerUser(Mockito.any(RegisterRequest.class));

        AuthenticationController controller = new AuthenticationController(mockService);
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        ResponseEntity<CustomResponseBody<AuthenticationResponse>> response =
                controller.register(registerRequest);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
    }



    @Test
    void testLogin_Success() throws Exception {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);
        AuthenticationResponse authResponse = new AuthenticationResponse("testToken");

        when(mockService.authenticateUser(Mockito.any(AuthenticateRequest.class)))
                .thenReturn(authResponse);

        AuthenticationController controller = new AuthenticationController(mockService);

        AuthenticateRequest authenticateRequest = new AuthenticateRequest();
        authenticateRequest.setEmail("test@example.com");
        authenticateRequest.setPassword("password123");

        ResponseEntity<CustomResponseBody<AuthenticationResponse>> response =
                controller.login(authenticateRequest);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("User login successfully", response.getBody().message());
        Assertions.assertEquals("testToken", response.getBody().data().getToken());
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);

        when(mockService.authenticateUser(Mockito.any(AuthenticateRequest.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        AuthenticationController controller = new AuthenticationController(mockService);

        AuthenticateRequest authenticateRequest = new AuthenticateRequest();
        authenticateRequest.setEmail("test@example.com");
        authenticateRequest.setPassword("password123");

        ResponseEntity<CustomResponseBody<AuthenticationResponse>> response =
                controller.login(authenticateRequest);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("User not found", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testVerifyOtp_Success() throws Exception {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);

        AuthenticationResponse authResponse = new AuthenticationResponse("testToken");

        when(mockService.verifyOtp(Mockito.any(OtpVarificationRequest.class)))
                .thenReturn(authResponse);

        AuthenticationController controller = new AuthenticationController(mockService);

        OtpVarificationRequest otpRequest = new OtpVarificationRequest();
        otpRequest.setOtp("123456");

        ResponseEntity<CustomResponseBody<AuthenticationResponse>> response =
                controller.verifyOtp(otpRequest);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Verified email successfully", response.getBody().message());
        Assertions.assertEquals("testToken", response.getBody().data().getToken());
    }

    @Test
    void testVerifyOtp_TokenExpired() throws Exception {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);

        when(mockService.verifyOtp(Mockito.any(OtpVarificationRequest.class)))
                .thenThrow(new TokenExpiredException("OTP expired"));

        AuthenticationController controller = new AuthenticationController(mockService);

        OtpVarificationRequest otpRequest = new OtpVarificationRequest();
        otpRequest.setOtp("123456");

        ResponseEntity<CustomResponseBody<AuthenticationResponse>> response =
                controller.verifyOtp(otpRequest);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("OTP expired", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testLogin_InvalidCredentials() {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);

        when(mockService.authenticateUser(Mockito.any(AuthenticateRequest.class)))
                .thenThrow(new UserNotFoundException("Invalid credentials"));

        AuthenticationController controller = new AuthenticationController(mockService);

        AuthenticateRequest request = new AuthenticateRequest();
        request.setEmail("invalid@example.com");
        request.setPassword("wrongPassword");

        ResponseEntity<CustomResponseBody<AuthenticationResponse>> response = controller.login(request);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("User not found", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }


    @Test
    void testVerifyOtp_InvalidOtp() {

        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);

        when(mockService.verifyOtp(Mockito.any(OtpVarificationRequest.class)))
                .thenThrow(new TokenExpiredException("Invalid OTP"));

        AuthenticationController controller = new AuthenticationController(mockService);

        OtpVarificationRequest request = new OtpVarificationRequest();
        request.setOtp("wrongOtp");

        ResponseEntity<CustomResponseBody<AuthenticationResponse>> response = controller.verifyOtp(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Invalid OTP", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testResendOtp_Success() {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);
        AuthenticationController controller = new AuthenticationController(mockService);

        ResponseEntity<CustomResponseBody<String>> response = controller.resendOtp();

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("OTP resent successfully", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testResendOtp_Failure() {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);

        Mockito.doThrow(new RuntimeException("Unexpected error"))
                .when(mockService)
                .resendOtp();

        AuthenticationController controller = new AuthenticationController(mockService);

        ResponseEntity<CustomResponseBody<String>> response = controller.resendOtp();

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Unexpected error", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testPasswordReset_Success() {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);
        AuthenticationController controller = new AuthenticationController(mockService);

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setPassword("newPassword123");
        request.setToken("resetToken");

        ResponseEntity<CustomResponseBody<String>> response = controller.resetPassword(request);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Password reset successfully", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testPasswordReset_TokenExpired() {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);

        Mockito.doThrow(new TokenExpiredException("Token expired"))
                .when(mockService)
                .resetPassword(Mockito.any(ResetPasswordRequest.class));

        AuthenticationController controller = new AuthenticationController(mockService);

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setPassword("newPassword123");
        request.setToken("expiredToken");

        ResponseEntity<CustomResponseBody<String>> response = controller.resetPassword(request);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("The password reset token has expired. Please request a new one.", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testPasswordReset_UserNotFound() {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);

        Mockito.doThrow(new UserNotFoundException("User not found"))
                .when(mockService)
                .resetPassword(Mockito.any(ResetPasswordRequest.class));

        AuthenticationController controller = new AuthenticationController(mockService);

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setPassword("newPassword123");
        request.setToken("validToken");

        ResponseEntity<CustomResponseBody<String>> response = controller.resetPassword(request);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("User not found", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testLogin_Success_Logging() throws Exception {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);
        AuthenticationResponse authResponse = new AuthenticationResponse("testToken");

        when(mockService.authenticateUser(Mockito.any(AuthenticateRequest.class)))
                .thenReturn(authResponse);

        AuthenticationController controller = new AuthenticationController(mockService);

        AuthenticateRequest authenticateRequest = new AuthenticateRequest();
        authenticateRequest.setEmail("test@example.com");
        authenticateRequest.setPassword("password123");

        ResponseEntity<CustomResponseBody<AuthenticationResponse>> response = controller.login(authenticateRequest);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(mockService).authenticateUser(Mockito.any(AuthenticateRequest.class));
    }

    @Test
    void testForgotPassword_Success() {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);
        AuthenticationController controller = new AuthenticationController(mockService);

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("user@example.com");

        HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);

        ResponseEntity<CustomResponseBody<String>> response = controller.forgotPassword(servletRequest, request);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SUCCESS", response.getBody().result().name());
        Assertions.assertEquals("Reset link sent successfully", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testForgotPassword_UserNotFound() {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);

        Mockito.doThrow(new UserNotFoundException("User not found"))
                .when(mockService)
                .handleForgotPassword(Mockito.any(HttpServletRequest.class), Mockito.anyString());

        AuthenticationController controller = new AuthenticationController(mockService);

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("nonexistentuser@example.com");

        HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);

        ResponseEntity<CustomResponseBody<String>> response = controller.forgotPassword(servletRequest, request);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("User not found", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }

    @Test
    void testForgotPassword_UnexpectedError() {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);

        Mockito.doThrow(new RuntimeException("Unexpected error"))
                .when(mockService)
                .handleForgotPassword(Mockito.any(HttpServletRequest.class), Mockito.anyString());

        AuthenticationController controller = new AuthenticationController(mockService);

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("user@example.com");

        HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);

        ResponseEntity<CustomResponseBody<String>> response = controller.forgotPassword(servletRequest, request);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("FAILURE", response.getBody().result().name());
        Assertions.assertEquals("Something went wrong", response.getBody().message());
        Assertions.assertNull(response.getBody().data());
    }
}


