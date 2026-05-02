package com.circleguard.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest
class PromotionToNotificationIntegrationTest {

    @Autowired
    private ExposureNotificationListener listener;

    @MockBean
    private NotificationDispatcher dispatcher;

    @MockBean
    private LmsService lmsService;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void shouldDispatchNotificationOnStatusChange() throws Exception {
        String eventJson = "{\"anonymousId\":\"user123\", \"status\":\"SUSPECT\"}";

        listener.handleStatusChange(eventJson);

        verify(dispatcher).dispatch("user123", "SUSPECT");
        verify(lmsService).syncRemoteAttendance("user123", "SUSPECT");
    }
}
