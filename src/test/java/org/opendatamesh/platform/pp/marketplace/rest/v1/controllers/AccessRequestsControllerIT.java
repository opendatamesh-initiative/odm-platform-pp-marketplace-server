package org.opendatamesh.platform.pp.marketplace.rest.v1.controllers;

import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.opendatamesh.platform.pp.marketplace.rest.v1.MarketplaceApplicationIT;
import org.opendatamesh.platform.pp.marketplace.rest.v1.RoutesV1;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests.AccessRequestRes;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.events.ExecutorResultReceivedEvent;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.MarketplaceExecutorResponseRes;
import org.opendatamesh.platform.pp.marketplace.utils.client.jackson.PageUtility;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.opendatamesh.platform.pp.marketplace.client.ExecutorClient;
import org.opendatamesh.platform.pp.marketplace.client.NotificationClient;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class AccessRequestsControllerIT extends MarketplaceApplicationIT {

    @Autowired
    private ExecutorClient executorClient;

    @Autowired
    private NotificationClient notificationClient;

    @BeforeEach
    void setUp() {
        Mockito.reset(executorClient, notificationClient);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(executorClient, notificationClient);
    }

    @Test
    void testSubmitAccessRequest() {
        // Setup
        AccessRequestRes accessRequest = new AccessRequestRes();
        accessRequest.setIdentifier("test-request-1");
        accessRequest.setName("Test Request");
        accessRequest.setOperation(AccessRequestRes.AccessRequestOperationRes.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product");
        accessRequest.setProviderDataProductPortsFqn(Arrays.asList("test-port-1", "test-port-2"));
        accessRequest.setConsumerType("test-type");
        accessRequest.setConsumerIdentifier("test-consumer-1");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day

        HttpEntity<AccessRequestRes> requestEntity = new HttpEntity<>(accessRequest);

        // Execute
        ResponseEntity<AccessRequestRes> response = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/submit"),
                HttpMethod.POST,
                requestEntity,
                AccessRequestRes.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(accessRequest.getIdentifier(), response.getBody().getIdentifier());
        assertEquals(accessRequest.getName(), response.getBody().getName());
        String uuid = response.getBody().getUuid();

        // Verify that executorClient.postRequest was called
        Mockito.verify(executorClient, Mockito.atLeastOnce()).postRequest(Mockito.anyString(), Mockito.any());

        // Teardown
        rest.exchange(
            apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
            HttpMethod.DELETE,
            null,
            Void.class
        );
    }

    @Test
    void testGetAccessRequest() {
        // Setup
        AccessRequestRes accessRequest = new AccessRequestRes();
        accessRequest.setIdentifier("test-request-2");
        accessRequest.setName("Test Request 2");
        accessRequest.setOperation(AccessRequestRes.AccessRequestOperationRes.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product");
        accessRequest.setProviderDataProductPortsFqn(Arrays.asList("test-port-1", "test-port-2"));
        accessRequest.setConsumerType("test-type");
        accessRequest.setConsumerIdentifier("test-consumer-2");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day

        HttpEntity<AccessRequestRes> requestEntity = new HttpEntity<>(accessRequest);
        
        // First create an access request
        ResponseEntity<AccessRequestRes> createResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/submit"),
                HttpMethod.POST,
                requestEntity,
                AccessRequestRes.class
        );
        assertNotNull(createResponse.getBody());
        String uuid = createResponse.getBody().getUuid();

        // Execute
        ResponseEntity<AccessRequestRes> response = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
                HttpMethod.GET,
                null,
                AccessRequestRes.class
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(uuid, response.getBody().getUuid());

        // Teardown
        rest.exchange(
            apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
            HttpMethod.DELETE,
            null,
            Void.class
        );
    }

    @Test
    void testSearchAccessRequests() {
        // Setup
        AccessRequestRes accessRequest = new AccessRequestRes();
        accessRequest.setIdentifier("test-request-3");
        accessRequest.setName("Test Request 3");
        accessRequest.setOperation(AccessRequestRes.AccessRequestOperationRes.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product");
        accessRequest.setProviderDataProductPortsFqn(Arrays.asList("test-port-1", "test-port-2"));
        accessRequest.setConsumerType("test-type");
        accessRequest.setConsumerIdentifier("test-consumer-3");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day

        HttpEntity<AccessRequestRes> requestEntity = new HttpEntity<>(accessRequest);
        
        // First create an access request
        ResponseEntity<AccessRequestRes> createResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/submit"),
                HttpMethod.POST,
                requestEntity,
                AccessRequestRes.class
        );
        assertNotNull(createResponse.getBody());
        String uuid = createResponse.getBody().getUuid();

        // Execute
        ResponseEntity<PageUtility<AccessRequestRes>> response = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<AccessRequestRes>>() {}
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getTotalElements() > 0);

        // Teardown
        rest.exchange(
            apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
            HttpMethod.DELETE,
            null,
            Void.class
        );
    }

    @Test
    void testSearchAccessRequestsWhenNoneExist() {
        // Execute
        ResponseEntity<PageUtility<AccessRequestRes>> response = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<AccessRequestRes>>() {}
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getTotalElements());
        assertTrue(response.getBody().getContent().isEmpty());
    }

    @Test
    void testDeleteAccessRequest() {
        // Setup
        AccessRequestRes accessRequest = new AccessRequestRes();
        accessRequest.setIdentifier("test-request-4");
        accessRequest.setName("Test Request 4");
        accessRequest.setOperation(AccessRequestRes.AccessRequestOperationRes.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product");
        accessRequest.setProviderDataProductPortsFqn(Arrays.asList("test-port-1", "test-port-2"));
        accessRequest.setConsumerType("test-type");
        accessRequest.setConsumerIdentifier("test-consumer-4");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day

        HttpEntity<AccessRequestRes> requestEntity = new HttpEntity<>(accessRequest);
        
        // First create an access request
        ResponseEntity<AccessRequestRes> createResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/submit"),
                HttpMethod.POST,
                requestEntity,
                AccessRequestRes.class
        );
        assertNotNull(createResponse.getBody());
        String uuid = createResponse.getBody().getUuid();

        // Execute
        ResponseEntity<Void> response = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Verify
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verify the request is actually deleted
        ResponseEntity<AccessRequestRes> getResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
                HttpMethod.GET,
                null,
                AccessRequestRes.class
        );
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
        // Teardown (idempotent, in case delete failed)
        rest.exchange(
            apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
            HttpMethod.DELETE,
            null,
            Void.class
        );
    }

    @Test
    void testHandleExecutorResponse() {
        // Setup
        AccessRequestRes accessRequest = new AccessRequestRes();
        accessRequest.setIdentifier("test-request-5");
        accessRequest.setName("Test Request 5");
        accessRequest.setOperation(AccessRequestRes.AccessRequestOperationRes.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product");
        accessRequest.setProviderDataProductPortsFqn(Arrays.asList("test-port-1", "test-port-2"));
        accessRequest.setConsumerType("USER");
        accessRequest.setConsumerIdentifier("test-consumer-5");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day

        HttpEntity<AccessRequestRes> requestEntity = new HttpEntity<>(accessRequest);
        
        // First create an access request
        ResponseEntity<AccessRequestRes> createResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/submit"),
                HttpMethod.POST,
                requestEntity,
                AccessRequestRes.class
        );
        assertNotNull(createResponse.getBody());
        String uuid = createResponse.getBody().getUuid();

        // Create executor response
        MarketplaceExecutorResponseRes executorResponse = new MarketplaceExecutorResponseRes();
        executorResponse.setAccessRequestIdentifier(uuid);
        executorResponse.setStatus(MarketplaceExecutorResponseRes.ExecutorResponseStatus.GRANTED);
        executorResponse.setMessage("Test success message");
        
        MarketplaceExecutorResponseRes.ProviderInfo providerInfo = new MarketplaceExecutorResponseRes.ProviderInfo();
        providerInfo.setDataProductFqn("test-product");
        providerInfo.setDataProductPortsFqn(Arrays.asList("test-port-1"));
        executorResponse.setProvider(providerInfo);

        HttpEntity<MarketplaceExecutorResponseRes> responseEntity = new HttpEntity<>(executorResponse);

        // Stub notificationClient.notifyEvent to do nothing
        Mockito.doNothing().when(notificationClient).notifyEvent(Mockito.any());

        // Execute
        ResponseEntity<Void> response = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + executorResponse.getAccessRequestIdentifier() + "/results"),
                HttpMethod.POST,
                responseEntity,
                Void.class
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Capture the event sent to notificationClient
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        Mockito.verify(notificationClient, Mockito.atLeastOnce()).notifyEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        // Assert event content
        assertInstanceOf(ExecutorResultReceivedEvent.class, event);
        ExecutorResultReceivedEvent execEvent = (ExecutorResultReceivedEvent) event;
        ExecutorResultReceivedEvent.ExecutorResultReceivedEventState afterState = execEvent.getAfterState();
        assertNotNull(afterState);
        ExecutorResultReceivedEvent.ExecutorResultReceivedEventAccessRequest eventAccessRequest = afterState.getAccessRequest();
        assertNotNull(eventAccessRequest);
        assertEquals("USER", eventAccessRequest.getConsumerType().name());
        assertEquals(accessRequest.getIdentifier(), eventAccessRequest.getIdentifier());

        // Teardown
        rest.exchange(
            apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
            HttpMethod.DELETE,
            null,
            Void.class
        );
    }
} 