package com.dalhousie.FundFusion.exception;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenInvalidExaptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String errorMessage = "Invalid token provided";

        TokenInvalidExaption exception = new TokenInvalidExaption(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }
}

