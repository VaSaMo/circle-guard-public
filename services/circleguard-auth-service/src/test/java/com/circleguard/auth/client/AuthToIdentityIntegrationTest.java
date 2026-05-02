package com.circleguard.auth.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AuthToIdentityIntegrationTest {

    @Autowired
    private IdentityClient identityClient;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldRetrieveAnonymousId() {
        UUID expectedId = UUID.randomUUID();
        String response = "{\"anonymousId\":\"" + expectedId + "\"}";

        mockServer.expect(requestTo("http://localhost:8083/api/v1/identities/map"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        UUID actualId = identityClient.getAnonymousId("test@example.com");

        assertEquals(expectedId, actualId);
    }
}
