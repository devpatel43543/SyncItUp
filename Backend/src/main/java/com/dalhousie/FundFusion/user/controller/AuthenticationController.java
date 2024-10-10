package com.dalhousie.FundFusion.user.controller;

import com.dalhousie.FundFusion.exaption.UserNotFoundException;
import com.dalhousie.FundFusion.user.repository.UserRepository;
import com.dalhousie.FundFusion.user.requestEntity.AuthenticateRequest;
import com.dalhousie.FundFusion.user.requestEntity.RegisterRequest;
import com.dalhousie.FundFusion.user.responseEntity.AuthenticationResponse;
import com.dalhousie.FundFusion.user.service.userAuthenticationService.UserService;
import com.dalhousie.FundFusion.util.CustomResponseBody;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/check")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final UserService userService;
    private final UserRepository userRepository;
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
//    @PostMapping("/login")
//    public ResponseEntity<Object> login(@Valid @RequestBody AuthenticateRequest authenticateRequest) {
//        if (userRepository.findByEmail(authenticateRequest.getEmail()).isEmpty()) {
//            throw new UserNotFoundException("User not found with email: " + authenticateRequest.getEmail());
//        }
//        try{
//            log.info("Authenticating user: {}", authenticateRequest.getEmail());
//            AuthenticationResponse authenticationResponse = userService.authenticateUser(authenticateRequest);
//            CustomResponseBody<AuthenticationResponse> responseBody = new CustomResponseBody<AuthenticationResponse>(CustomResponseBody.Result.SUCCESS,authenticationResponse,"user login successfully");
//            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
//
//        }catch (Exception e) {
//            CustomResponseBody<AuthenticationResponse> responseBody = new CustomResponseBody<AuthenticationResponse>(CustomResponseBody.Result.FAILURE,null,"something went wrong");
//            return ResponseEntity.status(BAD_REQUEST).body(responseBody);
//        }
//    }

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



}
