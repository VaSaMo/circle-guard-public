package com.circleguard.auth.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.boot.test.mock.mockito.MockBean;
import com.circleguard.auth.repository.LocalUserRepository;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VisitorHandoffFlowE2E {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void visitorHandoffFlow() throws Exception {
        String handoffJson = "{\"anonymousId\":\"00000000-0000-0000-0000-000000000001\"}";
        
        mockMvc.perform(post("/api/v1/auth/visitor/handoff")
                .content(handoffJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
