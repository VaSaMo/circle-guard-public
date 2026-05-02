package com.circleguard.identity.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IdentityValidatorTest {

    private final IdentityValidator validator = new IdentityValidator();

    @Test
    void shouldValidateCorrectEmail() {
        assertTrue(validator.isValidIdentity("test@example.com"));
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertFalse(validator.isValidIdentity("invalid-email"));
    }

    @Test
    void shouldRejectNullOrEmpty() {
        assertFalse(validator.isValidIdentity(null));
        assertFalse(validator.isValidIdentity(""));
        assertFalse(validator.isValidIdentity("   "));
    }
}
