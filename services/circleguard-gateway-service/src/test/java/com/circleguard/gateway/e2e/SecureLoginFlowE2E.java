package com.circleguard.gateway.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecureLoginFlowE2E {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void completeLoginAndAccessFlow() throws Exception {
        // Simulating a full flow through the gateway
        // 1. Attempt to login (mocked or routed to Auth service)
        // Since this is a gateway test, we simulate the entry point
        
        // In a real E2E, we'd use a TestRestTemplate to call actual running services
        // But here we use MockMvc for the sake of the exercise within the Spring context
        
        mockMvc.perform(post("/api/v1/gate/validate")
                .content("{\"token\":\"dummy-token\"}")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }
}
