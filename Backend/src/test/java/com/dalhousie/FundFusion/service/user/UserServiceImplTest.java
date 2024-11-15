package com.dalhousie.FundFusion.service.user;

import com.dalhousie.FundFusion.exception.UserNotFoundException;
import com.dalhousie.FundFusion.user.entity.User;
import com.dalhousie.FundFusion.user.repository.UserRepository;
import com.dalhousie.FundFusion.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setName("Test User");
    }

    @Test
    public void testGetUser_UserExists() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUser(1);

        assertEquals(user, result);
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    public void testGetUser_UserDoesNotExist() {

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(1));
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    public void testCheckValidUser_UserExists() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        boolean isValid = userService.checkValidUser(1);

        assertTrue(isValid);
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    public void testCheckValidUser_UserDoesNotExist() {

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.checkValidUser(1);
        });

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    public void testGetCurrentUser_UserIsAuthenticated() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.getCurrentUser();

        assertEquals(user, result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testGetCurrentUser_UserNotAuthenticated() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getCurrentUser());
        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    public void testGetCurrentUser_UserNotFound() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }
}
