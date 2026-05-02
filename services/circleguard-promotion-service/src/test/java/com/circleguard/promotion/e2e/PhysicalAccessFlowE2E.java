package com.circleguard.promotion.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.neo4j.core.Neo4jClient;
import com.circleguard.promotion.repository.graph.UserNodeRepository;

import org.springframework.test.context.ActiveProfiles;

import com.circleguard.promotion.service.CircleService;
import java.util.Collections;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PhysicalAccessFlowE2E {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CircleService circleService;

    @MockBean
    private org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void checkUserStatusForAccess() throws Exception {
        when(circleService.getUserCircles("test-user")).thenReturn(Collections.emptyList());
        
        mockMvc.perform(get("/api/v1/circles/user/test-user"))
                .andExpect(status().isOk());
    }
}
