package com.circleguard.promotion.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;
import com.circleguard.promotion.service.HealthStatusService;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminCorrectionFlowE2E {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthStatusService healthStatusService;

    @MockBean
    private org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @WithMockUser(roles = "HEALTH_CENTER")
    void adminCorrectsUserStatus() throws Exception {
        String correctionJson = "{\"anonymousId\":\"user123\", \"adminOverride\":true}";
        
        mockMvc.perform(post("/api/v1/health/resolve")
                .content(correctionJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
