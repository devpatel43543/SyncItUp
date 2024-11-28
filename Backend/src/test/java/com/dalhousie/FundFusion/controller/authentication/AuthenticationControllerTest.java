package com.dalhousie.FundFusion.controller.authentication;

import com.dalhousie.FundFusion.authentication.controller.AuthenticationController;
import com.dalhousie.FundFusion.authentication.requestEntity.*;
import com.dalhousie.FundFusion.authentication.responseEntity.AuthenticationResponse;
import com.dalhousie.FundFusion.authentication.service.AuthenticationService;
import com.dalhousie.FundFusion.exception.TokenExpiredException;
import com.dalhousie.FundFusion.exception.UserNotFoundException;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegister_Success() throws Exception {
        AuthenticationService mockService = Mockito.mock(AuthenticationService.class);

        AuthenticationResponse authResponse = new AuthenticationResponse("testToken");

        Mockito.when(mockService.registerUser(Mockito.any(RegisterRequest.class)))
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

        Mockito.when(mockService.authenticateUser(Mockito.any(AuthenticateRequest.class)))
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

        Mockito.when(mockService.authenticateUser(Mockito.any(AuthenticateRequest.class)))
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

        Mockito.when(mockService.verifyOtp(Mockito.any(OtpVarificationRequest.class)))
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

        Mockito.when(mockService.verifyOtp(Mockito.any(OtpVarificationRequest.class)))
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
}


