package com.dalhousie.FundFusion.user.service.userAuthenticationService;


import com.dalhousie.FundFusion.exaption.UserAlreadyExistException;
import com.dalhousie.FundFusion.jwt.JwtService;
import com.dalhousie.FundFusion.user.Entity.User;
import com.dalhousie.FundFusion.user.repository.UserRepository;
import com.dalhousie.FundFusion.user.requestEntity.AuthenticateRequest;
import com.dalhousie.FundFusion.user.requestEntity.RegisterRequest;
import com.dalhousie.FundFusion.user.responseEntity.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
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

}