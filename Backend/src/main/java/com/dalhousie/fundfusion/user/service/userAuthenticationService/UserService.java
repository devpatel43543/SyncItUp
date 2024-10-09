package com.dalhousie.fundfusion.user.service.userAuthenticationService;

import com.dalhousie.fundfusion.user.requestEntity.AuthenticateRequest;
import com.dalhousie.fundfusion.user.requestEntity.RegisterRequest;
import com.dalhousie.fundfusion.user.responseEntity.AuthenticationResponse;

public interface UserService {
    AuthenticationResponse registerUser(RegisterRequest registerRequest);

    AuthenticationResponse authenticateUser(AuthenticateRequest authenticateRequest);

}
