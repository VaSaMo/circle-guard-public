package com.circleguard.identity.util;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class IdentityValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public boolean isValidIdentity(String identity) {
        if (identity == null || identity.isBlank()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(identity).matches();
    }
}
