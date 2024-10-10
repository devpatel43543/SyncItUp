package com.dalhousie.FundFusion.user.service.userAuthenticationService;

import com.dalhousie.FundFusion.user.requestEntity.AuthenticateRequest;
import com.dalhousie.FundFusion.user.requestEntity.RegisterRequest;
import com.dalhousie.FundFusion.user.responseEntity.AuthenticationResponse;

public interface UserService {
    AuthenticationResponse registerUser(RegisterRequest registerRequest);

    AuthenticationResponse authenticateUser(AuthenticateRequest authenticateRequest);

}