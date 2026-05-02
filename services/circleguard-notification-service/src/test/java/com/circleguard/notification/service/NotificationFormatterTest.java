package com.circleguard.notification.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class NotificationFormatterTest {

    private final TemplateService templateService = new TemplateService(mock(freemarker.template.Configuration.class));

    @Test
    void shouldGenerateCorrectPushContentForSuspect() {
        String content = templateService.generatePushContent("SUSPECT");
        assertEquals("Health Alert: Your status is SUSPECT. Tap for isolation steps.", content);
    }

    @Test
    void shouldGenerateCorrectPushContentForProbable() {
        String content = templateService.generatePushContent("PROBABLE");
        assertEquals("Health Alert: You are now PROBABLE due to area exposure. Monitor symptoms and maintain distance.", content);
    }

    @Test
    void shouldGenerateCorrectSmsContent() {
        String content = templateService.generateSmsContent("CONFIRMED");
        assertEquals("CircleGuard Alert: Your health status is now CONFIRMED. Please check your email for details and guidelines.", content);
    }
}
