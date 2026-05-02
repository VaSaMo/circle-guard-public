package com.circleguard.promotion.util;

import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class StatusTransitionValidator {
    private static final Set<String> VALID_STATUSES = Set.of("ACTIVE", "SUSPECT", "PROBABLE", "CONFIRMED", "RECOVERED");

    public boolean isValidTransition(String currentStatus, String newStatus) {
        if (!VALID_STATUSES.contains(newStatus)) {
            return false;
        }
        if ("CONFIRMED".equals(currentStatus) && "ACTIVE".equals(newStatus)) {
            return false; // Should go through RECOVERED or need override
        }
        return true;
    }
}
