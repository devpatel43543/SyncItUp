package com.dalhousie.FundFusion.user.controller;

import com.dalhousie.FundFusion.exaption.UserNotFoundException;
import com.dalhousie.FundFusion.user.repository.PasswordResetTokenRepository;
import com.dalhousie.FundFusion.user.repository.UserRepository;
import com.dalhousie.FundFusion.user.requestEntity.AuthenticateRequest;
import com.dalhousie.FundFusion.user.requestEntity.ForgotPasswordRequest;
import com.dalhousie.FundFusion.user.requestEntity.RegisterRequest;
import com.dalhousie.FundFusion.user.requestEntity.ResetPasswordRequest;
import com.dalhousie.FundFusion.user.responseEntity.AuthenticationResponse;
import com.dalhousie.FundFusion.user.service.userAuthenticationService.UserService;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
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
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

//    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<CustomResponseBody<AuthenticationResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthenticationResponse authenticationResponse = userService.registerUser(registerRequest);
            log.info("User registered successfully with email: {}", registerRequest.getEmail());
            CustomResponseBody<AuthenticationResponse> responseBody =new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS,authenticationResponse,"user registered successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        }  catch (Exception e) {
            log.error("Unexpected error during user registration: {}", e.getMessage());
            CustomResponseBody<AuthenticationResponse> responseBody = new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<CustomResponseBody<AuthenticationResponse>> login(@Valid @RequestBody AuthenticateRequest authenticateRequest) {
        try {
            log.info("Authenticating user: {}", authenticateRequest.getEmail());
            AuthenticationResponse authenticationResponse = userService.authenticateUser(authenticateRequest);
            CustomResponseBody<AuthenticationResponse> responseBody = new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, authenticationResponse, "user login successfully");
            return ResponseEntity.status(HttpStatus.OK).body(responseBody); // Changed status to OK
        } catch (UserNotFoundException e) {
            log.error("Authentication failed: {}", e.getMessage());
            CustomResponseBody<AuthenticationResponse> responseBody = new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
        } catch (Exception e) {
            log.error("Unexpected error during user login: {}", e.getMessage());
            CustomResponseBody<AuthenticationResponse> responseBody = new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }
    }


    @PostMapping("/forgot_password")
    public ResponseEntity<String> forgotPassword(HttpServletRequest servletRequest, @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            String resetUrl = userService.getURL(servletRequest) + "/password_reset";
            log.info(resetUrl);
            userService.forgotPassword(forgotPasswordRequest.getEmail(), resetUrl);
            return ResponseEntity.status(HttpStatus.OK).body("Reset link sent successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }


    @PostMapping("/password_reset")
    public ResponseEntity<String> resetPassword(@RequestParam String token,@RequestParam String email, @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try{
            userService.resetPassword(resetPasswordRequest.getEmail(),resetPasswordRequest.getPassword(),resetPasswordRequest.getToken());
            return ResponseEntity.status(HttpStatus.OK).body("request sent successfully");
        }catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }




    }
