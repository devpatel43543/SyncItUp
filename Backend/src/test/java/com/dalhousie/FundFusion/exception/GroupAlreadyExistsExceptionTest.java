package com.dalhousie.FundFusion.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupAlreadyExistsExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Arrange
        String errorMessage = "Group already exists";

        // Act
        GroupAlreadyExistsException exception = new GroupAlreadyExistsException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
}
