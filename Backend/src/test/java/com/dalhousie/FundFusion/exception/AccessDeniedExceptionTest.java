package com.dalhousie.FundFusion.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccessDeniedExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Arrange
        String errorMessage = "Access is denied";

        // Act
        AccessDeniedException exception = new AccessDeniedException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
}