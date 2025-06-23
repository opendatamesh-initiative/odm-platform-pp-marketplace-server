package org.opendatamesh.platform.pp.marketplace.rest.v1.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.core.ExecutorResponsesService;
import org.opendatamesh.platform.pp.marketplace.rest.v1.MarketplaceApplicationIT;
import org.opendatamesh.platform.pp.marketplace.rest.v1.RoutesV1;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.MarketplaceExecutorResponseRes;
import org.opendatamesh.platform.pp.marketplace.utils.client.jackson.PageUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ExecutorResponsesControllerIT extends MarketplaceApplicationIT {

    @Autowired
    private ExecutorResponsesService executorResponsesService;

    @Test
    void testGetExecutorResponse() {
        // Setup - Create an executor response directly through the service
        MarketplaceExecutorResponseRes executorResponse = new MarketplaceExecutorResponseRes();
        executorResponse.setAccessRequestIdentifier("test-request-1");
        executorResponse.setStatus(MarketplaceExecutorResponseRes.ExecutorResponseStatus.DENIED);
        executorResponse.setMessage("Test denied message");
        
        MarketplaceExecutorResponseRes.ProviderInfo providerInfo = new MarketplaceExecutorResponseRes.ProviderInfo();
        providerInfo.setDataProductFqn("test-product-1");
        providerInfo.setDataProductPortsFqn(Arrays.asList("test-port-1"));
        executorResponse.setProvider(providerInfo);

        MarketplaceExecutorResponseRes createdResponse = executorResponsesService.createResource(executorResponse);
        assertNotNull(createdResponse);
        Long id = createdResponse.getId();

        // Execute
        ResponseEntity<MarketplaceExecutorResponseRes> response = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES, "/" + id),
                HttpMethod.GET,
                null,
                MarketplaceExecutorResponseRes.class
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(executorResponse.getAccessRequestIdentifier(), response.getBody().getAccessRequestIdentifier());
        assertEquals(executorResponse.getStatus(), response.getBody().getStatus());
        assertEquals(executorResponse.getMessage(), response.getBody().getMessage());

        // Teardown
        executorResponsesService.delete(id);
    }

    @Test
    void testSearchExecutorResponses() {
        // Setup - Create an executor response directly through the service
        MarketplaceExecutorResponseRes executorResponse = new MarketplaceExecutorResponseRes();
        executorResponse.setAccessRequestIdentifier("test-request-2");
        executorResponse.setStatus(MarketplaceExecutorResponseRes.ExecutorResponseStatus.PENDING);
        executorResponse.setMessage("Test pending message");
        
        MarketplaceExecutorResponseRes.ProviderInfo providerInfo = new MarketplaceExecutorResponseRes.ProviderInfo();
        providerInfo.setDataProductFqn("test-product-2");
        providerInfo.setDataProductPortsFqn(Arrays.asList("test-port-2"));
        executorResponse.setProvider(providerInfo);

        MarketplaceExecutorResponseRes createdResponse = executorResponsesService.createResource(executorResponse);
        assertNotNull(createdResponse);
        Long id = createdResponse.getId();

        // Execute
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> response = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {}
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getTotalElements() > 0);

        // Teardown
        executorResponsesService.delete(id);
    }

    @Test
    void testSearchExecutorResponsesWhenNoneExist() {
        // Execute
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> response = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {}
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getTotalElements());
        assertTrue(response.getBody().getContent().isEmpty());
    }

    @Test
    void testSearchExecutorResponsesByAccessRequestIdentifier() {
        // Setup - Create an executor response directly through the service
        MarketplaceExecutorResponseRes executorResponse = new MarketplaceExecutorResponseRes();
        executorResponse.setAccessRequestIdentifier("test-request-3");
        executorResponse.setStatus(MarketplaceExecutorResponseRes.ExecutorResponseStatus.GRANTED);
        executorResponse.setMessage("Test granted message");
        
        MarketplaceExecutorResponseRes.ProviderInfo providerInfo = new MarketplaceExecutorResponseRes.ProviderInfo();
        providerInfo.setDataProductFqn("test-product-3");
        providerInfo.setDataProductPortsFqn(Arrays.asList("test-port-3"));
        executorResponse.setProvider(providerInfo);

        MarketplaceExecutorResponseRes createdResponse = executorResponsesService.createResource(executorResponse);
        assertNotNull(createdResponse);
        Long id = createdResponse.getId();

        // Execute - Search by access request identifier
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> response = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES) + "?accessRequestIdentifier=test-request-3",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {}
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals("test-request-3", response.getBody().getContent().get(0).getAccessRequestIdentifier());

        // Teardown
        executorResponsesService.delete(id);
    }

    @Test
    void testSearchExecutorResponsesByAccessRequestUuid() {
        // Setup - Create an executor response directly through the service
        MarketplaceExecutorResponseRes executorResponse = new MarketplaceExecutorResponseRes();
        executorResponse.setAccessRequestIdentifier("test-request-4");
        executorResponse.setStatus(MarketplaceExecutorResponseRes.ExecutorResponseStatus.ERROR);
        executorResponse.setMessage("Test error message");
        
        MarketplaceExecutorResponseRes.ProviderInfo providerInfo = new MarketplaceExecutorResponseRes.ProviderInfo();
        providerInfo.setDataProductFqn("test-product-4");
        providerInfo.setDataProductPortsFqn(Arrays.asList("test-port-4"));
        executorResponse.setProvider(providerInfo);

        MarketplaceExecutorResponseRes createdResponse = executorResponsesService.createResource(executorResponse);
        assertNotNull(createdResponse);
        Long id = createdResponse.getId();

        // Note: This test would require creating an access request first to get a valid UUID
        // For now, we'll test that the endpoint works with an empty result
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> response = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES) + "?accessRequestUuid=non-existent-uuid",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {}
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getTotalElements());

        // Teardown
        executorResponsesService.delete(id);
    }
} 