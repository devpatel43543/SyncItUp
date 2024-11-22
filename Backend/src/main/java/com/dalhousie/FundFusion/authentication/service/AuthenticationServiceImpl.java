    package com.dalhousie.FundFusion.authentication.service;


    import com.dalhousie.FundFusion.exception.TokenExpiredException;
    import com.dalhousie.FundFusion.exception.TokenInvalidExaption;
    import com.dalhousie.FundFusion.exception.UserAlreadyExistException;
    import com.dalhousie.FundFusion.exception.UserNotFoundException;
    import com.dalhousie.FundFusion.group.entity.Group;
    import com.dalhousie.FundFusion.group.entity.PendingGroupMembers;
    import com.dalhousie.FundFusion.group.entity.UserGroup;
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
        @Transactional
        @Override
        public AuthenticationResponse registerUser(RegisterRequest registerRequest) {
            if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
                new UserAlreadyExistException("provided user is already exists");
            }
            var user = User.builder()
                    .name(registerRequest.getName())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .isEmailVerified(false)
                    .build();
            userRepository.save(user);
            registerdUserId = user.getId();
            Otp otp = otpService.generateOtp(user.getId());
            log.info("line 77: {}", otp);
            otpMailBody(otp.getOtp(), user.getEmail());



            return AuthenticationResponse.builder()
                    .token(null)
                    .build();
        }

        @Override
        public AuthenticationResponse authenticateUser(AuthenticateRequest authenticateRequest) {
            log.info("line 46: Authenticating user: {}", authenticateRequest.getEmail());
            log.info("line 47: Authenticating user: {}",userRepository.findByEmail(authenticateRequest.getEmail()).orElse(null));
            if(userRepository.findByEmail(authenticateRequest.getEmail()).isEmpty()){
                log.info("line 48");
                new UsernameNotFoundException("User not found with email: "+authenticateRequest.getEmail());
            }
            log.info("line 52: Authenticating user: {}",authenticateRequest.getEmail());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken
                            (authenticateRequest.getEmail(),
                                    authenticateRequest.getPassword()));
            log.info("line 56");
            var user = userRepository.findByEmail(authenticateRequest.getEmail()).get();
            log.info("line 58:{}",user);

            if (!user.isEmailVerified()) {
                throw new RuntimeException("Email not verified. Please verify before logging in.");
            }
            var jwtToken = jwtService.generateToken(user,user.isEmailVerified());
            log.info("line 59:{} ", jwtToken);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }

        @Override
        public String getURL(HttpServletRequest request) {String siteURL = request.getRequestURL().toString().replace(request.getServletPath(), "");
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
        @Override
        public void forgotPassword(String email, String resetUrl) {
            try {
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
                log.info("User found: {}", user);

                String resetToken = resetTokenService.createResetPasswordToken(user.getId()).getToken();
                log.info("Generated token: {}", resetToken);

                String resetPasswordLink = resetUrl + "?email=" + email + "&token=" + resetToken;
                log.info("Reset password link: {}", resetPasswordLink);

                //sendMail(resetPasswordLink, email);
                forgotPasswordMailBody(resetPasswordLink,email);
            } catch (Exception e) {
                log.error("Error in forgotPassword: {}", e.getMessage());
                throw e;  // rethrow to handle at the controller level
            }
        }

        private void forgotPasswordMailBody(String resetPasswordLink, String email) {
            String subject = "Reset Your Password";
            String content = "<p>Hello,</p>"
                    + "<p>You have requested to reset your password. Please click the link below to create a new password. This link is valid for only 5 minutes for your security.</p>"
                    + "<p>If you did not request this change, please ignore this email.</p>"
                    + "<p>Click the link below to reset your password:</p>"
                    + "<p><a href=\"" + resetPasswordLink + "\">Reset My Password</a></p>"
                    + "<p>For your safety, please do not share this link with anyone.</p>"
                    + "<p>Thank you!</p>";
            sendMail(subject,content,email);
        }

        private void otpMailBody(String otp,String mail){
            String subject = "Verify Your Email";
            String content = "<p>Hello,</p>"
                    + "<p>Your OTP for email verification is:</p>"
                    + "<h2>" + otp + "</h2>"
                    + "<p>This OTP is valid for 5 minutes.</p>";
            sendMail(subject,content,mail);
        }

        @Override
        public void resetPassword(String email, String password, String token) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

            PasswordReset passwordReset = resetTokenService.findByUserId(user.getId())
                    .orElseThrow(() -> new TokenInvalidExaption("User did not initiate a reset password request."));

            if (!Objects.equals(token, passwordReset.getToken())) {
                throw new TokenInvalidExaption("Failed to authenticate token. Please request to reset your password again.");
            }

            if (!resetTokenService.isTokenValid(passwordReset)) {
                throw new TokenInvalidExaption("Token expired. Please request to reset your password again.");
            }

            String newPassword = passwordEncoder.encode(password);
            userRepository.updatePassword(email, newPassword);

            resetTokenService.deleteResetPasswordToken(passwordReset);
        }

        @Override
        public AuthenticationResponse verifyOtp(OtpVarificationRequest otpVarificationRequest) {
            Optional<Otp> otpOptional = otpService.findByOtp(otpVarificationRequest.getOtp());
            if (otpOptional.isEmpty()) {
                throw new TokenExpiredException("Invalid OTP. Please try again.");
            }
            Otp otp = otpOptional.get();
            if (!otpService.isOtpValid(otp)) {
                throw new TokenExpiredException("OTP has expired. Please request a new one.");
            }
            //mark user verified
            User user = userRepository.findById(otp.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + otp.getUserId()));
            user.setEmailVerified(true);
            userRepository.save(user);

            String jwtToken = jwtService.generateToken(user, user.isEmailVerified());

            otpService.deleteOtp(otp);

//            CustomResponseBody<String> responseBody = new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, "Email verified successfully", "OTP verified successfully.");
            return AuthenticationResponse.builder().token(jwtToken).build();
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