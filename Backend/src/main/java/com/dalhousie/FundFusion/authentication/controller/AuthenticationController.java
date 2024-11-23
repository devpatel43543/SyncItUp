package com.dalhousie.FundFusion.authentication.controller;

import com.dalhousie.FundFusion.authentication.requestEntity.*;
import com.dalhousie.FundFusion.exception.TokenExpiredException;
import com.dalhousie.FundFusion.exception.UserNotFoundException;
import com.dalhousie.FundFusion.authentication.responseEntity.AuthenticationResponse;
import com.dalhousie.FundFusion.authentication.service.AuthenticationService;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/check")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<CustomResponseBody<AuthenticationResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.registerUser(registerRequest);
            log.info("User registered successfully with email: {}", registerRequest.getEmail());

            HttpStatus status = HttpStatus.CREATED;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "User registered successfully, verify email";

            return buildResponse(status, result, authenticationResponse, message);
        } catch (Exception e) {
            log.error("Unexpected error during user registration: {}", e.getMessage());
            return buildResponse(HttpStatus.BAD_REQUEST, CustomResponseBody.Result.FAILURE, null, "Something went wrong");
        }
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<CustomResponseBody<AuthenticationResponse>> verifyOtp(@RequestBody OtpVarificationRequest otpVarificationRequest) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.verifyOtp(otpVarificationRequest);

            HttpStatus status = HttpStatus.CREATED;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Verified email successfully";

            return buildResponse(status, result, authenticationResponse, message);
        } catch (TokenExpiredException e) {
            log.error("OTP expired: {}", e.getMessage());
            return buildResponse(HttpStatus.BAD_REQUEST, CustomResponseBody.Result.FAILURE, null, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, CustomResponseBody.Result.FAILURE, null, "Something went wrong");
        }
    }

    @PostMapping("/resendOtp")
    public ResponseEntity<CustomResponseBody<String>> resendOtp() {
        try {
            authenticationService.resendOtp();
            return buildResponse(
                    HttpStatus.CREATED,
                    CustomResponseBody.Result.SUCCESS,
                    null,
                    "OTP resent successfully"
            );
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return buildResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    CustomResponseBody.Result.FAILURE,
                    null,
                    e.getMessage()
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CustomResponseBody<AuthenticationResponse>> login(@Valid @RequestBody AuthenticateRequest authenticateRequest) {
        try {
            log.info("Authenticating user: {}", authenticateRequest.getEmail());
            AuthenticationResponse authenticationResponse = authenticationService.authenticateUser(authenticateRequest);
            return buildResponse(
                    HttpStatus.OK,
                    CustomResponseBody.Result.SUCCESS,
                    authenticationResponse,
                    "User login successfully"
            );
        } catch (UserNotFoundException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return buildResponse(
                    HttpStatus.NOT_FOUND,
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "User not found"
            );
        } catch (Exception e) {
            log.error("Unexpected error during user login: {}", e.getMessage());
            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong"
            );
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<CustomResponseBody<String>> forgotPassword(HttpServletRequest servletRequest, @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            String resetUrl = authenticationService.getURL(servletRequest) + "/resetPassword";
            log.info(resetUrl);
            authenticationService.forgotPassword(forgotPasswordRequest.getEmail(), resetUrl);
            return buildResponse(
                    HttpStatus.OK,
                    CustomResponseBody.Result.SUCCESS,
                    null,
                    "Reset link sent successfully"
            );
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return buildResponse(
                    HttpStatus.NOT_FOUND,
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "User not found"
            );
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return buildResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    CustomResponseBody.Result.FAILURE,
                    null,
                    "Something went wrong"
            );
        }
    }

    @PostMapping("/passwordReset")
    public ResponseEntity<CustomResponseBody<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            String email = resetPasswordRequest.getEmail();
            String password = resetPasswordRequest.getPassword();
            String token = resetPasswordRequest.getToken();

            authenticationService.resetPassword(email, password, token);

            HttpStatus status = HttpStatus.OK;
            CustomResponseBody.Result result = CustomResponseBody.Result.SUCCESS;
            String message = "Password reset successfully";

            return buildResponse(status, result, null, message);
        } catch (TokenExpiredException e) {
            log.error("Token expired: {}", e.getMessage());

            HttpStatus status = HttpStatus.BAD_REQUEST;
            CustomResponseBody.Result result = CustomResponseBody.Result.FAILURE;
            String message = "The password reset token has expired. Please request a new one.";

            return buildResponse(status, result, null, message);
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return buildResponse(HttpStatus.NOT_FOUND, CustomResponseBody.Result.FAILURE, null, "User not found");
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, CustomResponseBody.Result.FAILURE, null, "Something went wrong");
        }
    }

    // Centralized Helper Method for Responses
    private <T> ResponseEntity<CustomResponseBody<T>> buildResponse(HttpStatus status, CustomResponseBody.Result result, T data, String message) {
        CustomResponseBody<T> responseBody = new CustomResponseBody<>(result, data, message);
        return ResponseEntity.status(status).body(responseBody);
    }
}
