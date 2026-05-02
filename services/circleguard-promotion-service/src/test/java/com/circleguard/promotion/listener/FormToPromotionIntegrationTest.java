package com.circleguard.promotion.listener;

import com.circleguard.promotion.service.HealthStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static org.mockito.Mockito.verify;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FormToPromotionIntegrationTest {

    @Autowired
    private SurveyListener surveyListener;

    @MockBean
    private HealthStatusService healthStatusService;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void shouldUpdateStatusOnSurveySubmittedWithSymptoms() {
        Map<String, Object> event = Map.of(
                "anonymousId", "user123",
                "hasSymptoms", true);

        surveyListener.onSurveySubmitted(event);

        verify(healthStatusService).updateStatus("user123", "SUSPECT");
    }
}
