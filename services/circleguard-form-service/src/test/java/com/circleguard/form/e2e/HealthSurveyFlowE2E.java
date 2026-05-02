package com.circleguard.form.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.mock.mockito.MockBean;
import com.circleguard.form.repository.HealthSurveyRepository;
import org.springframework.kafka.core.KafkaTemplate;
import com.circleguard.form.model.HealthSurvey;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class HealthSurveyFlowE2E {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthSurveyRepository repository;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void submitSurveyAndTriggerStatusChange() throws Exception {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        String surveyJson = "{\"anonymousId\":\"00000000-0000-0000-0000-000000000001\", \"responses\":{\"q1\":\"YES\"}}";
        
        mockMvc.perform(post("/api/v1/surveys")
                .content(surveyJson)
                .contentType("application/json"))
                .andExpect(status().isOk());
    }
}
