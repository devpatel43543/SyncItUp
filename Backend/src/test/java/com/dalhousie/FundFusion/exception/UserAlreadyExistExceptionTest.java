package com.dalhousie.FundFusion.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserAlreadyExistExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Arrange
        String errorMessage = "User already exists";

        // Act
        UserAlreadyExistException exception = new UserAlreadyExistException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
}
