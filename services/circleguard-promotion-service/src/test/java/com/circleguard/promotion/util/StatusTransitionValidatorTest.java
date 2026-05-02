package com.circleguard.promotion.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatusTransitionValidatorTest {

    private final StatusTransitionValidator validator = new StatusTransitionValidator();

    @Test
    void shouldAllowValidTransition() {
        assertTrue(validator.isValidTransition("ACTIVE", "SUSPECT"));
    }

    @Test
    void shouldRejectInvalidStatus() {
        assertFalse(validator.isValidTransition("ACTIVE", "INVALID"));
    }

    @Test
    void shouldRejectConfirmedToActiveWithoutRecovery() {
        assertFalse(validator.isValidTransition("CONFIRMED", "ACTIVE"));
    }

    @Test
    void shouldAllowRecoveredToActive() {
        assertTrue(validator.isValidTransition("RECOVERED", "ACTIVE"));
    }
}
