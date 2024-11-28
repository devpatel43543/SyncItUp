package com.dalhousie.FundFusion.service.config;

import com.dalhousie.FundFusion.config.JwtAuthenticationFilter;
import com.dalhousie.FundFusion.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Mock
    private JwtAuthenticationFilter jwtFilter;

    @Mock
    private AuthenticationProvider authenticationProvider;

    private MockMvc mockMvc;

//    @Test
//    void testSecurityFilterChain() throws Exception {
//        SecurityFilterChain securityFilterChain = securityConfig.securityFilterChain(null);
//        assertNotNull(securityFilterChain);
//    }

    @Test
    void testSecurityFilterChainLocal() throws Exception {
        MockitoAnnotations.openMocks(this);

        SecurityFilterChain securityFilterChain = securityConfig.securityFilterChainLocal(null);
        assertNotNull(securityFilterChain);
    }
}


