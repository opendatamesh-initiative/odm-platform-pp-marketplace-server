package org.opendatamesh.platform.pp.marketplace.rest.v1.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.pp.marketplace.client.ExecutorClient;
import org.opendatamesh.platform.pp.marketplace.client.NotificationClient;
import org.opendatamesh.platform.pp.marketplace.rest.v1.MarketplaceApplicationIT;
import org.opendatamesh.platform.pp.marketplace.rest.v1.RoutesV1;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests.AccessRequestRes;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.MarketplaceExecutorResponseRes;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.MarketplaceExecutorResponseUploadRes;
import org.opendatamesh.platform.pp.marketplace.utils.client.jackson.PageUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ExecutorResponsesControllerIT extends MarketplaceApplicationIT {

    @Autowired
    private ExecutorClient executorClient;

    @Autowired
    private NotificationClient notificationClient;

    @BeforeEach
    void setUp() {
        Mockito.reset(executorClient, notificationClient);
        // Mock executor client to do nothing when called (to prevent exceptions during access request submission)
        Mockito.doNothing().when(executorClient).postRequest(Mockito.anyString(), Mockito.any());
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(executorClient, notificationClient);
    }

    @Test
    void testGetExecutorResponse() {
        // Setup - Create an access request first
        AccessRequestRes accessRequest = new AccessRequestRes();
        accessRequest.setIdentifier("test-request-1");
        accessRequest.setName("Test Request 1");
        accessRequest.setOperation(AccessRequestRes.AccessRequestOperationRes.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product-1");
        accessRequest.setProviderDataProductPortsFqn(Arrays.asList("test-port-1"));
        accessRequest.setConsumerType("USER");
        accessRequest.setConsumerIdentifier("test-consumer-1");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day

        HttpEntity<AccessRequestRes> requestEntity = new HttpEntity<>(accessRequest);
        ResponseEntity<AccessRequestRes> createResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/submit"),
                HttpMethod.POST,
                requestEntity,
                AccessRequestRes.class
        );
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        String uuid = createResponse.getBody().getUuid();
        assertNotNull(uuid, "Access request UUID should not be null");

        // Create executor response through endpoint
        MarketplaceExecutorResponseUploadRes executorResponseUpload = new MarketplaceExecutorResponseUploadRes();
        executorResponseUpload.setStatus(MarketplaceExecutorResponseUploadRes.ExecutorResponseStatus.DENIED);
        executorResponseUpload.setMessage("Test denied message");

        MarketplaceExecutorResponseUploadRes.ProviderInfo providerInfo = new MarketplaceExecutorResponseUploadRes.ProviderInfo();
        providerInfo.setDataProductFqn("test-product-1");
        providerInfo.setDataProductPortsFqn(Arrays.asList("test-port-1"));
        executorResponseUpload.setProvider(providerInfo);

        HttpEntity<MarketplaceExecutorResponseUploadRes> responseEntity = new HttpEntity<>(executorResponseUpload);
        Mockito.doNothing().when(notificationClient).notifyEvent(Mockito.any());

        ResponseEntity<Void> submitResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid + "/results"),
                HttpMethod.POST,
                responseEntity,
                Void.class
        );
        assertEquals(HttpStatus.OK, submitResponse.getStatusCode());

        // Get the created executor response ID by searching
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> searchResponse = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES) + "?accessRequestUuid=" + uuid,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {
                }
        );
        assertNotNull(searchResponse.getBody());
        assertEquals(1, searchResponse.getBody().getTotalElements());
        Long id = searchResponse.getBody().getContent().get(0).getId();

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
        
        // Create expected response for comparison
        MarketplaceExecutorResponseRes expectedResponse = new MarketplaceExecutorResponseRes();
        expectedResponse.setAccessRequestIdentifier(accessRequest.getIdentifier());
        expectedResponse.setAccessRequestUuid(uuid);
        expectedResponse.setStatus(MarketplaceExecutorResponseRes.ExecutorResponseStatus.DENIED);
        expectedResponse.setMessage("Test denied message");
        MarketplaceExecutorResponseRes.ProviderInfo expectedProviderInfo = new MarketplaceExecutorResponseRes.ProviderInfo();
        expectedProviderInfo.setDataProductFqn("test-product-1");
        expectedProviderInfo.setDataProductPortsFqn(Arrays.asList("test-port-1"));
        expectedResponse.setProvider(expectedProviderInfo);
        
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(expectedResponse);

        // Teardown
        rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    @Test
    void testSearchExecutorResponses() {
        // Setup - Create an access request first
        AccessRequestRes accessRequest = new AccessRequestRes();
        accessRequest.setIdentifier("test-request-2");
        accessRequest.setName("Test Request 2");
        accessRequest.setOperation(AccessRequestRes.AccessRequestOperationRes.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product-2");
        accessRequest.setProviderDataProductPortsFqn(Arrays.asList("test-port-2"));
        accessRequest.setConsumerType("USER");
        accessRequest.setConsumerIdentifier("test-consumer-2");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day

        HttpEntity<AccessRequestRes> requestEntity = new HttpEntity<>(accessRequest);
        ResponseEntity<AccessRequestRes> createResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/submit"),
                HttpMethod.POST,
                requestEntity,
                AccessRequestRes.class
        );
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        String uuid = createResponse.getBody().getUuid();
        assertNotNull(uuid, "Access request UUID should not be null");

        // Create executor response through endpoint
        MarketplaceExecutorResponseUploadRes executorResponseUpload = new MarketplaceExecutorResponseUploadRes();
        executorResponseUpload.setStatus(MarketplaceExecutorResponseUploadRes.ExecutorResponseStatus.PENDING);
        executorResponseUpload.setMessage("Test pending message");

        MarketplaceExecutorResponseUploadRes.ProviderInfo providerInfo = new MarketplaceExecutorResponseUploadRes.ProviderInfo();
        providerInfo.setDataProductFqn("test-product-2");
        providerInfo.setDataProductPortsFqn(Arrays.asList("test-port-2"));
        executorResponseUpload.setProvider(providerInfo);

        HttpEntity<MarketplaceExecutorResponseUploadRes> responseEntity = new HttpEntity<>(executorResponseUpload);
        Mockito.doNothing().when(notificationClient).notifyEvent(Mockito.any());

        ResponseEntity<Void> submitResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid + "/results"),
                HttpMethod.POST,
                responseEntity,
                Void.class
        );
        assertEquals(HttpStatus.OK, submitResponse.getStatusCode());

        // Execute
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> response = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getTotalElements() > 0);
        
        // Verify that accessRequestUuid is populated in the listing
        MarketplaceExecutorResponseRes createdResponse = response.getBody().getContent().stream()
                .filter(res -> uuid.equals(res.getAccessRequestUuid()))
                .findFirst()
                .orElse(null);
        assertNotNull(createdResponse, "Should find executor response with matching accessRequestUuid");
        assertEquals(uuid, createdResponse.getAccessRequestUuid(), "accessRequestUuid should be populated");
        assertEquals(accessRequest.getIdentifier(), createdResponse.getAccessRequestIdentifier());
        
        // Verify the response content using recursive comparison
        MarketplaceExecutorResponseRes expectedResponse = new MarketplaceExecutorResponseRes();
        expectedResponse.setAccessRequestIdentifier(accessRequest.getIdentifier());
        expectedResponse.setAccessRequestUuid(uuid);
        expectedResponse.setStatus(MarketplaceExecutorResponseRes.ExecutorResponseStatus.PENDING);
        expectedResponse.setMessage("Test pending message");
        MarketplaceExecutorResponseRes.ProviderInfo expectedProviderInfo = new MarketplaceExecutorResponseRes.ProviderInfo();
        expectedProviderInfo.setDataProductFqn("test-product-2");
        expectedProviderInfo.setDataProductPortsFqn(Arrays.asList("test-port-2"));
        expectedResponse.setProvider(expectedProviderInfo);
        
        assertThat(createdResponse)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(expectedResponse);

        // Teardown
        rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    @Test
    void testSearchExecutorResponsesWhenNoneExist() {
        // Execute
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> response = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {
                }
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getTotalElements());
        assertTrue(response.getBody().getContent().isEmpty());
    }

    @Test
    void testSearchExecutorResponsesByAccessRequestIdentifier() {
        // Setup - Create an access request first
        AccessRequestRes accessRequest = new AccessRequestRes();
        accessRequest.setIdentifier("test-request-3");
        accessRequest.setName("Test Request 3");
        accessRequest.setOperation(AccessRequestRes.AccessRequestOperationRes.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product-3");
        accessRequest.setProviderDataProductPortsFqn(Arrays.asList("test-port-3"));
        accessRequest.setConsumerType("USER");
        accessRequest.setConsumerIdentifier("test-consumer-3");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day

        HttpEntity<AccessRequestRes> requestEntity = new HttpEntity<>(accessRequest);
        ResponseEntity<AccessRequestRes> createResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/submit"),
                HttpMethod.POST,
                requestEntity,
                AccessRequestRes.class
        );
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        String uuid = createResponse.getBody().getUuid();
        assertNotNull(uuid, "Access request UUID should not be null");

        // Create executor response through endpoint
        MarketplaceExecutorResponseUploadRes executorResponseUpload = new MarketplaceExecutorResponseUploadRes();
        executorResponseUpload.setStatus(MarketplaceExecutorResponseUploadRes.ExecutorResponseStatus.GRANTED);
        executorResponseUpload.setMessage("Test granted message");

        MarketplaceExecutorResponseUploadRes.ProviderInfo providerInfo = new MarketplaceExecutorResponseUploadRes.ProviderInfo();
        providerInfo.setDataProductFqn("test-product-3");
        providerInfo.setDataProductPortsFqn(Arrays.asList("test-port-3"));
        executorResponseUpload.setProvider(providerInfo);

        HttpEntity<MarketplaceExecutorResponseUploadRes> responseEntity = new HttpEntity<>(executorResponseUpload);
        Mockito.doNothing().when(notificationClient).notifyEvent(Mockito.any());

        ResponseEntity<Void> submitResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid + "/results"),
                HttpMethod.POST,
                responseEntity,
                Void.class
        );
        assertEquals(HttpStatus.OK, submitResponse.getStatusCode());

        // Execute - Search by access request identifier
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> response = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES) + "?accessRequestIdentifier=test-request-3",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {
                }
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        
        MarketplaceExecutorResponseRes foundResponse = response.getBody().getContent().get(0);
        assertEquals("test-request-3", foundResponse.getAccessRequestIdentifier());
        assertEquals(uuid, foundResponse.getAccessRequestUuid(), "accessRequestUuid should be populated");
        
        // Verify the response content using recursive comparison
        MarketplaceExecutorResponseRes expectedResponse = new MarketplaceExecutorResponseRes();
        expectedResponse.setAccessRequestIdentifier(accessRequest.getIdentifier());
        expectedResponse.setAccessRequestUuid(uuid);
        expectedResponse.setStatus(MarketplaceExecutorResponseRes.ExecutorResponseStatus.GRANTED);
        expectedResponse.setMessage("Test granted message");
        MarketplaceExecutorResponseRes.ProviderInfo expectedProviderInfo = new MarketplaceExecutorResponseRes.ProviderInfo();
        expectedProviderInfo.setDataProductFqn("test-product-3");
        expectedProviderInfo.setDataProductPortsFqn(Arrays.asList("test-port-3"));
        expectedResponse.setProvider(expectedProviderInfo);
        
        assertThat(foundResponse)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(expectedResponse);

        // Teardown
        rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    @Test
    void testSearchExecutorResponsesByAccessRequestUuid() {
        // Setup - Create an access request first
        AccessRequestRes accessRequest = new AccessRequestRes();
        accessRequest.setIdentifier("test-request-4");
        accessRequest.setName("Test Request 4");
        accessRequest.setOperation(AccessRequestRes.AccessRequestOperationRes.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product-4");
        accessRequest.setProviderDataProductPortsFqn(Arrays.asList("test-port-4"));
        accessRequest.setConsumerType("USER");
        accessRequest.setConsumerIdentifier("test-consumer-4");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day

        HttpEntity<AccessRequestRes> requestEntity = new HttpEntity<>(accessRequest);
        ResponseEntity<AccessRequestRes> createResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/submit"),
                HttpMethod.POST,
                requestEntity,
                AccessRequestRes.class
        );
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        String uuid = createResponse.getBody().getUuid();
        assertNotNull(uuid, "Access request UUID should not be null");

        // Create executor response through endpoint
        MarketplaceExecutorResponseUploadRes executorResponseUpload = new MarketplaceExecutorResponseUploadRes();
        executorResponseUpload.setStatus(MarketplaceExecutorResponseUploadRes.ExecutorResponseStatus.ERROR);
        executorResponseUpload.setMessage("Test error message");

        MarketplaceExecutorResponseUploadRes.ProviderInfo providerInfo = new MarketplaceExecutorResponseUploadRes.ProviderInfo();
        providerInfo.setDataProductFqn("test-product-4");
        providerInfo.setDataProductPortsFqn(Arrays.asList("test-port-4"));
        executorResponseUpload.setProvider(providerInfo);

        HttpEntity<MarketplaceExecutorResponseUploadRes> responseEntity = new HttpEntity<>(executorResponseUpload);
        Mockito.doNothing().when(notificationClient).notifyEvent(Mockito.any());

        ResponseEntity<Void> submitResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid + "/results"),
                HttpMethod.POST,
                responseEntity,
                Void.class
        );
        assertEquals(HttpStatus.OK, submitResponse.getStatusCode());

        // Execute - Search by access request UUID
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> response = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES) + "?accessRequestUuid=" + uuid,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {
                }
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        
        MarketplaceExecutorResponseRes foundResponse = response.getBody().getContent().get(0);
        assertEquals(accessRequest.getIdentifier(), foundResponse.getAccessRequestIdentifier());
        assertEquals(uuid, foundResponse.getAccessRequestUuid(), "accessRequestUuid should be populated");
        
        // Verify the response content using recursive comparison
        MarketplaceExecutorResponseRes expectedResponse = new MarketplaceExecutorResponseRes();
        expectedResponse.setAccessRequestIdentifier(accessRequest.getIdentifier());
        expectedResponse.setAccessRequestUuid(uuid);
        expectedResponse.setStatus(MarketplaceExecutorResponseRes.ExecutorResponseStatus.ERROR);
        expectedResponse.setMessage("Test error message");
        MarketplaceExecutorResponseRes.ProviderInfo expectedProviderInfo = new MarketplaceExecutorResponseRes.ProviderInfo();
        expectedProviderInfo.setDataProductFqn("test-product-4");
        expectedProviderInfo.setDataProductPortsFqn(Arrays.asList("test-port-4"));
        expectedResponse.setProvider(expectedProviderInfo);
        
        assertThat(foundResponse)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(expectedResponse);

        // Teardown
        rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    @Test
    void testSearchExecutorResponsesByStatus() {
        // Setup - Create an access request first
        AccessRequestRes accessRequest = new AccessRequestRes();
        accessRequest.setIdentifier("test-request-status");
        accessRequest.setName("Test Request Status");
        accessRequest.setOperation(AccessRequestRes.AccessRequestOperationRes.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product-status");
        accessRequest.setProviderDataProductPortsFqn(Arrays.asList("test-port-status"));
        accessRequest.setConsumerType("USER");
        accessRequest.setConsumerIdentifier("test-consumer-status");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day

        HttpEntity<AccessRequestRes> requestEntity = new HttpEntity<>(accessRequest);
        ResponseEntity<AccessRequestRes> createResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/submit"),
                HttpMethod.POST,
                requestEntity,
                AccessRequestRes.class
        );
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        String uuid = createResponse.getBody().getUuid();
        assertNotNull(uuid, "Access request UUID should not be null");

        // Create executor response through endpoint
        MarketplaceExecutorResponseUploadRes executorResponseUpload = new MarketplaceExecutorResponseUploadRes();
        executorResponseUpload.setStatus(MarketplaceExecutorResponseUploadRes.ExecutorResponseStatus.GRANTED);
        executorResponseUpload.setMessage("Test granted message");

        MarketplaceExecutorResponseUploadRes.ProviderInfo providerInfo = new MarketplaceExecutorResponseUploadRes.ProviderInfo();
        providerInfo.setDataProductFqn("test-product-status");
        providerInfo.setDataProductPortsFqn(Arrays.asList("test-port-status"));
        executorResponseUpload.setProvider(providerInfo);

        HttpEntity<MarketplaceExecutorResponseUploadRes> responseEntity = new HttpEntity<>(executorResponseUpload);
        Mockito.doNothing().when(notificationClient).notifyEvent(Mockito.any());

        ResponseEntity<Void> submitResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid + "/results"),
                HttpMethod.POST,
                responseEntity,
                Void.class
        );
        assertEquals(HttpStatus.OK, submitResponse.getStatusCode());

        // Get the created executor response ID by searching
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> searchResponse = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES) + "?accessRequestUuid=" + uuid,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {
                }
        );
        assertNotNull(searchResponse.getBody());
        assertEquals(1, searchResponse.getBody().getTotalElements());
        Long id = searchResponse.getBody().getContent().get(0).getId();

        // Execute - Search by status GRANTED
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> response = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES) + "?status=GRANTED",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {
                }
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getTotalElements() >= 1);
        // Verify the created response is in the results
        MarketplaceExecutorResponseRes foundResponse = response.getBody().getContent().stream()
                .filter(res -> id.equals(res.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(foundResponse, "Should find executor response with GRANTED status");
        assertEquals(uuid, foundResponse.getAccessRequestUuid(), "accessRequestUuid should be populated");
        
        // Verify the response content using recursive comparison
        MarketplaceExecutorResponseRes expectedResponse = new MarketplaceExecutorResponseRes();
        expectedResponse.setAccessRequestIdentifier(accessRequest.getIdentifier());
        expectedResponse.setAccessRequestUuid(uuid);
        expectedResponse.setStatus(MarketplaceExecutorResponseRes.ExecutorResponseStatus.GRANTED);
        expectedResponse.setMessage("Test granted message");
        MarketplaceExecutorResponseRes.ProviderInfo expectedProviderInfo = new MarketplaceExecutorResponseRes.ProviderInfo();
        expectedProviderInfo.setDataProductFqn("test-product-status");
        expectedProviderInfo.setDataProductPortsFqn(Arrays.asList("test-port-status"));
        expectedResponse.setProvider(expectedProviderInfo);
        
        assertThat(foundResponse)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(expectedResponse);

        // Teardown
        rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    @Test
    void testSearchExecutorResponsesByDataProductPortsFqn() {
        // Setup - Create an access request first
        AccessRequestRes accessRequest = new AccessRequestRes();
        accessRequest.setIdentifier("test-request-ports");
        accessRequest.setName("Test Request Ports");
        accessRequest.setOperation(AccessRequestRes.AccessRequestOperationRes.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product-ports");
        accessRequest.setProviderDataProductPortsFqn(Arrays.asList("test-port-A", "test-port-B"));
        accessRequest.setConsumerType("USER");
        accessRequest.setConsumerIdentifier("test-consumer-ports");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day

        HttpEntity<AccessRequestRes> requestEntity = new HttpEntity<>(accessRequest);
        ResponseEntity<AccessRequestRes> createResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/submit"),
                HttpMethod.POST,
                requestEntity,
                AccessRequestRes.class
        );
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        String uuid = createResponse.getBody().getUuid();
        assertNotNull(uuid, "Access request UUID should not be null");

        // Create executor response through endpoint
        MarketplaceExecutorResponseUploadRes executorResponseUpload = new MarketplaceExecutorResponseUploadRes();
        executorResponseUpload.setStatus(MarketplaceExecutorResponseUploadRes.ExecutorResponseStatus.GRANTED);
        executorResponseUpload.setMessage("Test message");

        MarketplaceExecutorResponseUploadRes.ProviderInfo providerInfo = new MarketplaceExecutorResponseUploadRes.ProviderInfo();
        providerInfo.setDataProductFqn("test-product-ports");
        providerInfo.setDataProductPortsFqn(Arrays.asList("test-port-A", "test-port-B"));
        executorResponseUpload.setProvider(providerInfo);

        HttpEntity<MarketplaceExecutorResponseUploadRes> responseEntity = new HttpEntity<>(executorResponseUpload);
        Mockito.doNothing().when(notificationClient).notifyEvent(Mockito.any());

        ResponseEntity<Void> submitResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid + "/results"),
                HttpMethod.POST,
                responseEntity,
                Void.class
        );
        assertEquals(HttpStatus.OK, submitResponse.getStatusCode());

        // Get the created executor response ID by searching
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> searchResponse = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES) + "?accessRequestUuid=" + uuid,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {
                }
        );
        assertNotNull(searchResponse.getBody());
        assertEquals(1, searchResponse.getBody().getTotalElements());
        Long id = searchResponse.getBody().getContent().get(0).getId();

        // Execute - Search by port FQN
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> response = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES) + "?dataProductPortsFqn=test-port-A",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {
                }
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getTotalElements() >= 1);
        // Verify the created response is in the results
        MarketplaceExecutorResponseRes foundResponse = response.getBody().getContent().stream()
                .filter(res -> id.equals(res.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(foundResponse, "Should find executor response with test-port-A");
        assertEquals(uuid, foundResponse.getAccessRequestUuid(), "accessRequestUuid should be populated");
        
        // Verify the response content using recursive comparison
        MarketplaceExecutorResponseRes expectedResponse = new MarketplaceExecutorResponseRes();
        expectedResponse.setAccessRequestIdentifier(accessRequest.getIdentifier());
        expectedResponse.setAccessRequestUuid(uuid);
        expectedResponse.setStatus(MarketplaceExecutorResponseRes.ExecutorResponseStatus.GRANTED);
        expectedResponse.setMessage("Test message");
        MarketplaceExecutorResponseRes.ProviderInfo expectedProviderInfo = new MarketplaceExecutorResponseRes.ProviderInfo();
        expectedProviderInfo.setDataProductFqn("test-product-ports");
        expectedProviderInfo.setDataProductPortsFqn(Arrays.asList("test-port-A", "test-port-B"));
        expectedResponse.setProvider(expectedProviderInfo);
        
        assertThat(foundResponse)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(expectedResponse);

        // Teardown
        rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    @Test
    void testSearchExecutorResponsesByAccessRequestConsumerIdentifier() {
        // Setup - Create an access request with a specific consumer identifier
        AccessRequestRes accessRequest = new AccessRequestRes();
        accessRequest.setIdentifier("test-request-consumer");
        accessRequest.setName("Test Request Consumer");
        accessRequest.setOperation(AccessRequestRes.AccessRequestOperationRes.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product-consumer");
        accessRequest.setProviderDataProductPortsFqn(Arrays.asList("test-port-consumer"));
        accessRequest.setConsumerType("USER");
        accessRequest.setConsumerIdentifier("test-consumer-filter-123");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day

        HttpEntity<AccessRequestRes> requestEntity = new HttpEntity<>(accessRequest);

        // Create access request
        ResponseEntity<AccessRequestRes> createResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/submit"),
                HttpMethod.POST,
                requestEntity,
                AccessRequestRes.class
        );
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        String uuid = createResponse.getBody().getUuid();
        assertNotNull(uuid, "Access request UUID should not be null");

        // Create executor response linked to the access request through endpoint
        MarketplaceExecutorResponseUploadRes executorResponse = new MarketplaceExecutorResponseUploadRes();
        executorResponse.setStatus(MarketplaceExecutorResponseUploadRes.ExecutorResponseStatus.GRANTED);
        executorResponse.setMessage("Test message for consumer filter");

        MarketplaceExecutorResponseUploadRes.ProviderInfo providerInfo = new MarketplaceExecutorResponseUploadRes.ProviderInfo();
        providerInfo.setDataProductFqn("test-product-consumer");
        providerInfo.setDataProductPortsFqn(Arrays.asList("test-port-consumer"));
        executorResponse.setProvider(providerInfo);

        HttpEntity<MarketplaceExecutorResponseUploadRes> responseEntity = new HttpEntity<>(executorResponse);
        Mockito.doNothing().when(notificationClient).notifyEvent(Mockito.any());

        ResponseEntity<Void> submitResponse = rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid + "/results"),
                HttpMethod.POST,
                responseEntity,
                Void.class
        );
        assertEquals(HttpStatus.OK, submitResponse.getStatusCode());

        // Get the created executor response ID by searching
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> searchResponse = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES) + "?accessRequestUuid=" + uuid,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {
                }
        );
        assertNotNull(searchResponse.getBody());
        assertEquals(1, searchResponse.getBody().getTotalElements());
        Long executorResponseId = searchResponse.getBody().getContent().get(0).getId();

        // Execute - Search by consumer identifier
        ResponseEntity<PageUtility<MarketplaceExecutorResponseRes>> response = rest.exchange(
                apiUrl(RoutesV1.EXECUTOR_RESPONSES) + "?accessRequestConsumerIdentifier={consumerId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PageUtility<MarketplaceExecutorResponseRes>>() {
                },
                Map.of("consumerId", accessRequest.getConsumerIdentifier())
        );

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getTotalElements() >= 1);
        // Verify the created response is in the results
        MarketplaceExecutorResponseRes foundResponse = response.getBody().getContent().stream()
                .filter(res -> executorResponseId.equals(res.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(foundResponse, "Should find executor response linked to access request with consumer identifier");
        assertEquals(uuid, foundResponse.getAccessRequestUuid(), "accessRequestUuid should be populated");
        
        // Verify the response content using recursive comparison
        MarketplaceExecutorResponseRes expectedResponse = new MarketplaceExecutorResponseRes();
        expectedResponse.setAccessRequestIdentifier(accessRequest.getIdentifier());
        expectedResponse.setAccessRequestUuid(uuid);
        expectedResponse.setStatus(MarketplaceExecutorResponseRes.ExecutorResponseStatus.GRANTED);
        expectedResponse.setMessage("Test message for consumer filter");
        MarketplaceExecutorResponseRes.ProviderInfo expectedProviderInfo = new MarketplaceExecutorResponseRes.ProviderInfo();
        expectedProviderInfo.setDataProductFqn("test-product-consumer");
        expectedProviderInfo.setDataProductPortsFqn(Arrays.asList("test-port-consumer"));
        expectedResponse.setProvider(expectedProviderInfo);
        
        assertThat(foundResponse)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(expectedResponse);

        // Teardown
        rest.exchange(
                apiUrl(RoutesV1.ACCESS_REQUESTS, "/" + uuid),
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }
} 