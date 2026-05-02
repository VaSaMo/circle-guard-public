package com.circleguard.identity.service;

import com.circleguard.identity.model.IdentityMapping;
import com.circleguard.identity.repository.IdentityMappingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class IdentityToVaultIntegrationTest {

    @Autowired
    private IdentityVaultService identityVaultService;

    @MockBean
    private IdentityMappingRepository repository;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void shouldCreateNewMappingIfNotFound() {
        String realIdentity = "new-user@example.com";
        UUID generatedId = UUID.randomUUID();
        
        when(repository.findByIdentityHash(any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> {
            IdentityMapping mapping = invocation.getArgument(0);
            mapping.setAnonymousId(generatedId);
            return mapping;
        });

        UUID result = identityVaultService.getOrCreateAnonymousId(realIdentity);

        assertEquals(generatedId, result);
    }
}
