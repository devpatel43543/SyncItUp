package com.dalhousie.FundFusion.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberNotExistTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Arrange
        String errorMessage = "Member does not exist";

        // Act
        MemberNotExist exception = new MemberNotExist(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
}
