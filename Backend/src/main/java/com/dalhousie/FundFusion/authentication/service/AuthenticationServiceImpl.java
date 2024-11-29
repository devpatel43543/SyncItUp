package com.dalhousie.FundFusion.authentication.service;


import com.dalhousie.FundFusion.authentication.requestEntity.ResetPasswordRequest;
import com.dalhousie.FundFusion.exception.TokenExpiredException;
import com.dalhousie.FundFusion.exception.TokenInvalidExaption;
import com.dalhousie.FundFusion.exception.UserAlreadyExistException;
import com.dalhousie.FundFusion.exception.UserNotFoundException;
import com.dalhousie.FundFusion.group.repository.GroupRepository;
import com.dalhousie.FundFusion.group.repository.PendingGroupMembersRepository;
import com.dalhousie.FundFusion.group.repository.UserGroupRepository;
import com.dalhousie.FundFusion.jwt.JwtService;
import com.dalhousie.FundFusion.authentication.entity.Otp;
import com.dalhousie.FundFusion.authentication.entity.PasswordReset;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.authentication.repository.OtpRepository;
import com.dalhousie.FundFusion.user.repository.UserRepository;
import com.dalhousie.FundFusion.authentication.requestEntity.AuthenticateRequest;
import com.dalhousie.FundFusion.authentication.requestEntity.OtpVarificationRequest;
import com.dalhousie.FundFusion.authentication.requestEntity.RegisterRequest;
import com.dalhousie.FundFusion.authentication.responseEntity.AuthenticationResponse;
import com.dalhousie.FundFusion.util.EmailTemplates;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final GroupRepository groupRepository;
    private final PendingGroupMembersRepository pendingGroupMembershipRepository;
    private final UserGroupRepository userGroupRepository;
    private final int frontendPort = 80;
    private final ResetTokenService resetTokenService;
    private final JavaMailSender javaMailSender;
    private final OtpService otpService;
    private final OtpRepository otpRepository;
    private Integer registerdUserId;
    @Override
    @Transactional
    public AuthenticationResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("Provided user already exists");
        }

        var encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        var user = buildNewUser(registerRequest, encodedPassword);

        userRepository.save(user);
        registerdUserId = user.getId();

        Otp otp = otpService.generateOtp(user.getId());
        log.info("Generated OTP: {}", otp);

        otpMailBody(otp.getOtp(), user.getEmail());

        return AuthenticationResponse.builder()
                .token(null)
                .build();
    }

    // Helper method to build a new user
    private User buildNewUser(RegisterRequest registerRequest, String encodedPassword) {
        return User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(encodedPassword)
                .isEmailVerified(false)
                .build();
    }




    @Override
    public AuthenticationResponse authenticateUser(AuthenticateRequest authenticateRequest) {
        log.info("Authenticating user: {}", authenticateRequest.getEmail());

        var user = getUserByEmail(authenticateRequest.getEmail());
        authenticateUserCredentials(authenticateRequest);

        if (!user.isEmailVerified()) {
            throw new RuntimeException("Email not verified. Please verify before logging in.");
        }

        var jwtToken = jwtService.generateToken(user, user.isEmailVerified());
        log.info("Generated JWT Token: {}", jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    // Helper method to get user by email
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> userNotFound(email));
    }

    // Helper for exception
    private UsernameNotFoundException userNotFound(String email) {
        return new UsernameNotFoundException("User not found with email: " + email);
    }


    // Helper method to authenticate user credentials
    private void authenticateUserCredentials(AuthenticateRequest authenticateRequest) {
        var token = createAuthToken(authenticateRequest);
        authenticationManager.authenticate(token);
    }

    // Helper for token creation
    private UsernamePasswordAuthenticationToken createAuthToken(AuthenticateRequest authenticateRequest) {
        return new UsernamePasswordAuthenticationToken(
                authenticateRequest.getEmail(),
                authenticateRequest.getPassword()
        );
    }

    @Override
    public void handleForgotPassword(HttpServletRequest servletRequest, String email) {
        String resetUrl = getURL(servletRequest) + "/resetPassword";
        forgotPassword(email, resetUrl);
    }

    private String getURL(HttpServletRequest request) {String siteURL = request.getRequestURL().toString().replace(request.getServletPath(), "");
        try {
            java.net.URL oldURL = new java.net.URL(siteURL);

            // Use port only if running locally (optional)
            if ("localhost".equalsIgnoreCase(oldURL.getHost())) {
                return new java.net.URL(oldURL.getProtocol(), oldURL.getHost(), frontendPort, oldURL.getFile()).toString();
            }

            // On your VM, return the URL without a port
            return new java.net.URL(oldURL.getProtocol(), oldURL.getHost(), oldURL.getFile()).toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to construct the correct URL", e);
        }
    }

    private void forgotPassword(String email, String resetUrl) {
        User user = getUserByEmail(email);

        String resetToken = resetTokenService.createResetPasswordToken(user.getId()).getToken();
        log.info("Generated reset token: {}", resetToken);

        String resetPasswordLink = buildResetPasswordLink(resetUrl, email, resetToken);
        log.info("Reset password link: {}", resetPasswordLink);

        forgotPasswordMailBody(resetPasswordLink, email);
    }

    // Helper method to build reset password link
    private String buildResetPasswordLink(String resetUrl, String email, String token) {
        return resetUrl + "?email=" + email + "&token=" + token;
    }

    private void otpMailBody(String otp, String email) {
        String subject = "Verify Your Email";
        String content = buildOtpEmailContent(otp);
        sendMail(subject, content, email);
    }

    // Helper method to build OTP email content
    private String buildOtpEmailContent(String otp) {
        return String.format(EmailTemplates.getOtpEmailTemplate(),otp);
    }



    private void forgotPasswordMailBody(String resetPasswordLink, String email) {
        String subject = "Reset Your Password";
        String content = buildForgotPasswordEmailContent(resetPasswordLink);
        sendMail(subject, content, email);
    }

    // Helper method to build forgot password email content
    private String buildForgotPasswordEmailContent(String resetPasswordLink) {
        return String.format(EmailTemplates.getForgotPasswordEmailTemplate(), resetPasswordLink);
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = getUserByEmail(resetPasswordRequest.getEmail());
        PasswordReset passwordReset = getPasswordReset(user);

        validateToken(resetPasswordRequest.getToken(), passwordReset);

        String encodedPassword = passwordEncoder.encode(resetPasswordRequest.getPassword());
        userRepository.updatePassword(resetPasswordRequest.getEmail(), encodedPassword);

        resetTokenService.deleteResetPasswordToken(passwordReset);
    }

    // Helper method to get password reset token
    private PasswordReset getPasswordReset(User user) {
        return resetTokenService.findByUserId(user.getId())
                .orElseThrow(() -> resetRequestNotInitiated(user));
    }

    // Helper for exception
    private TokenInvalidExaption resetRequestNotInitiated(User user) {
        return new TokenInvalidExaption("User did not initiate a reset password request.");
    }


    // Helper method to validate token
    private void validateToken(String token, PasswordReset passwordReset) {
        if (!Objects.equals(token, passwordReset.getToken())) {
            throw new TokenInvalidExaption("Failed to authenticate token. Please request a new one.");
        }

        if (!resetTokenService.isTokenValid(passwordReset)) {
            throw new TokenInvalidExaption("Token expired. Please request a new one.");
        }
    }

    @Override
    public AuthenticationResponse verifyOtp(OtpVarificationRequest otpVarificationRequest) {
        Otp otp = getOtpByCode(otpVarificationRequest.getOtp());

        if (!otpService.isOtpValid(otp)) {
            throw new TokenExpiredException("OTP has expired. Please request a new one.");
        }

        User user = getUserById(otp.getUserId());
        markUserAsVerified(user);

        String jwtToken = jwtService.generateToken(user, true);

        otpService.deleteOtp(otp);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    // Helper method to get OTP by code
    private Otp getOtpByCode(String otpCode) {
        return otpService.findByOtp(otpCode)
                .orElseThrow(() -> new TokenExpiredException("Invalid OTP. Please try again."));
    }

    // Helper method to get user by ID
    private User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> userNotFoundById(userId));
    }

    // Helper for exception
    private UserNotFoundException userNotFoundById(Integer userId) {
        return new UserNotFoundException("User not found with ID: " + userId);
    }


    // Helper method to mark user as verified
    private void markUserAsVerified(User user) {
        user.setEmailVerified(true);
        userRepository.save(user);
    }
    @Transactional
    public void resendOtp(){
        log.info("line number 230:{}",registerdUserId);
        Otp otp = otpService.resendOtp(registerdUserId);
        log.info("line 232: {}", otp);
        Optional<User> user = userRepository.findById(registerdUserId);
        log.info("line number 234:{}",user.get().getEmail());
        if (!user.isPresent()) {
            throw new UserNotFoundException("User not found with ID: " + registerdUserId);
        }
        otpMailBody(otp.getOtp(), user.get().getEmail());
    }



    private void sendMail(String subject,String content, String email) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("devpatel43543@gmail.com", "dev");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);  // Ensure this line executes
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send reset password email: " + e.getMessage(), e);
        }
    }
}