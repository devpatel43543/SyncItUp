    package com.dalhousie.FundFusion.user.service.userAuthenticationService;


    import com.dalhousie.FundFusion.exaption.TokenInvalidExaption;
    import com.dalhousie.FundFusion.exaption.UserAlreadyExistException;
    import com.dalhousie.FundFusion.exaption.UserNotFoundException;
    import com.dalhousie.FundFusion.group.entity.Group;
    import com.dalhousie.FundFusion.group.entity.PendingGroupMembers;
    import com.dalhousie.FundFusion.group.entity.UserGroup;
    import com.dalhousie.FundFusion.group.repository.GroupRepository;
    import com.dalhousie.FundFusion.group.repository.PendingGroupMembersRepository;
    import com.dalhousie.FundFusion.group.repository.UserGroupRepository;
    import com.dalhousie.FundFusion.jwt.JwtService;
    import com.dalhousie.FundFusion.user.entity.PasswordReset;
    import com.dalhousie.FundFusion.user.entity.User;
    import com.dalhousie.FundFusion.user.repository.UserRepository;
    import com.dalhousie.FundFusion.user.requestEntity.AuthenticateRequest;
    import com.dalhousie.FundFusion.user.requestEntity.ForgotPasswordRequest;
    import com.dalhousie.FundFusion.user.requestEntity.RegisterRequest;
    import com.dalhousie.FundFusion.user.responseEntity.AuthenticationResponse;
    import com.dalhousie.FundFusion.user.service.passwordResetService.ResetTokenService;
    import jakarta.mail.MessagingException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.mail.javamail.JavaMailSender;
    import org.springframework.mail.javamail.MimeMessageHelper;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    import jakarta.mail.internet.MimeMessage;
    import java.io.UnsupportedEncodingException;
    import org.springframework.mail.javamail.MimeMessageHelper;

    import java.util.List;
    import java.util.Objects;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class UserServiceImpl implements UserService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final GroupRepository groupRepository;
        private final PendingGroupMembersRepository pendingGroupMembershipRepository;
        private final UserGroupRepository userGroupRepository;
        private final int frontendPort = 3000;
        private final ResetTokenService resetTokenService;
        private final JavaMailSender javaMailSender;
        @Transactional
        @Override
        public AuthenticationResponse registerUser(RegisterRequest registerRequest) {
            if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
                new UserAlreadyExistException("provided user is already exists");
            }
            var userModel = User.builder()
                    .name(registerRequest.getName())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .build();
            userRepository.save(userModel);
            var jwtToken = jwtService.generateToken(userModel);
            List<PendingGroupMembers> pendingGroupMemberships = pendingGroupMembershipRepository.findByEmail(registerRequest.getEmail());
            //transfer into group, registered user is in PendingGroupMembership table
            for (PendingGroupMembers pendingGroupMembership : pendingGroupMemberships) {

                Group group = pendingGroupMembership.getGroup();
                UserGroup userGroup = UserGroup.builder()
                        .user(userModel)
                        .group(group)
                        .userEmail(registerRequest.getEmail())
                        .build();
                userGroupRepository.save(userGroup);
                pendingGroupMembershipRepository.delete(pendingGroupMembership);
            }

            return AuthenticationResponse.builder()
                    .token(jwtToken)
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
            var jwtToken = jwtService.generateToken(user);
            log.info("line 59:{} ", jwtToken);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }

        @Override
        public String getURL(HttpServletRequest request) {
            // Generate the site URL without the servlet path
            String siteURL = request.getRequestURL().toString().replace(request.getServletPath(), "");

            try {
                java.net.URL oldURL = new java.net.URL(siteURL);
                // Create a new URL with the frontend port if necessary
                java.net.URL newURL = new java.net.URL(oldURL.getProtocol(), oldURL.getHost(), frontendPort, oldURL.getFile());
                return newURL.toString();
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

                String resetPasswordLink = resetUrl + "?token=" + resetToken + "&email=" + email;
                log.info("Reset password link: {}", resetPasswordLink);

                sendMail(resetPasswordLink, email);
            } catch (Exception e) {
                log.error("Error in forgotPassword: {}", e.getMessage());
                throw e;  // rethrow to handle at the controller level
            }
        }

        @Override
        public void resetPassword(String email, String password, String token) {
            // Step 1: Find user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

            // Step 2: Fetch the reset password token
            PasswordReset resetPasswordTokenModel = resetTokenService.findByUserId(user.getId())
                    .orElseThrow(() -> new TokenInvalidExaption("User did not initiate a reset password request."));

            // Step 3: Validate token
            if (!Objects.equals(token, resetPasswordTokenModel.getToken())) {
                throw new TokenInvalidExaption("Failed to authenticate token. Please request to reset your password again.");
            }

            // Step 4: Check if the token is valid (not expired)
            if (!resetTokenService.isTokenValid(resetPasswordTokenModel)) {
                throw new TokenInvalidExaption("Token expired. Please request to reset your password again.");
            }

            // Step 5: Update the user's password
            String newPassword = passwordEncoder.encode(password);
            userRepository.updatePassword(email, newPassword);

            // Step 6: Delete the reset token after successful password reset
            resetTokenService.deleteResetPasswordToken(resetPasswordTokenModel);
        }
        private void sendMail(String resetPasswordLink, String email) {
            String subject = "Reset Your Password";
            String content = "<p>Hello,</p>"
                    + "<p>You have requested to reset your password. Please click the link below to create a new password. This link is valid for only 5 minutes for your security.</p>"
                    + "<p>If you did not request this change, please ignore this email.</p>"
                    + "<p>Click the link below to reset your password:</p>"
                    + "<p><a href=\"" + resetPasswordLink + "\">Reset My Password</a></p>"
                    + "<p>For your safety, please do not share this link with anyone.</p>"
                    + "<p>Thank you!</p>";

            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setFrom("your-email@gmail.com", "Your Name");
                helper.setTo(email);
                helper.setSubject(subject);
                helper.setText(content, true);

                javaMailSender.send(message);  // Ensure this line executes
            } catch (MessagingException | UnsupportedEncodingException e) {
                throw new RuntimeException("Failed to send reset password email: " + e.getMessage(), e);
            }
        }
    }