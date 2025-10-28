package org.opendatamesh.platform.pp.marketplace.accessrequests.services.submitter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.configuration.executor.MarketplaceExecutorConfig;
import org.opendatamesh.platform.pp.marketplace.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.marketplace.exceptions.client.ClientException;
import org.opendatamesh.platform.pp.marketplace.utils.usecases.TransactionalOutboundPort;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccessRequestSubmitterTest {

    @Mock
    private AccessRequestSubmitterPresenter presenter;
    @Mock
    private AccessRequestSubmitterPersistencyOutputPort persistencyOutputPort;
    @Mock
    private AccessRequestSubmitterExecutorOutputPort executorOutputPort;
    @Mock
    private TransactionalOutboundPort transactionalOutboundPort;

    @BeforeEach
    void setUp() {
        lenient().doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transactionalOutboundPort).doInTransaction(any(Runnable.class));
    }

    private AccessRequest createTestAccessRequest() {
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setIdentifier("test-request-1");
        accessRequest.setConsumerIdentifier("test-consumer-1");
        accessRequest.setName("Test Request");
        accessRequest.setOperation(AccessRequest.AccessRequestOperation.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product");
        accessRequest.setProviderDataProductPortsFqn(Collections.singletonList("test-port"));
        accessRequest.setConsumerType("test-type");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day
        return accessRequest;
    }

    @Test
    void testExecuteWithValidRequest() {
        // Setup
        AccessRequest accessRequest = createTestAccessRequest();
        AccessRequestSubmitterCommand command = new AccessRequestSubmitterCommand();
        command.setAccessRequest(accessRequest);

        AccessRequestSubmitter submitter = new AccessRequestSubmitter(
                presenter,
                command,
                persistencyOutputPort,
                executorOutputPort,
                transactionalOutboundPort
        );

        when(persistencyOutputPort.accessRequestAlreadyExists(any(), eq(command.getAccessRequest().getOperation()))).thenReturn(false);
        when(persistencyOutputPort.createAccessRequest(any())).thenReturn(accessRequest);

        MarketplaceExecutorConfig.MarketplaceExecutor executor = new MarketplaceExecutorConfig.MarketplaceExecutor();
        executor.setName("test-executor");
        executor.setAddress("http://test-executor");
        executor.setActive(true);
        when(executorOutputPort.getExecutorsConfig()).thenReturn(Collections.singletonList(executor));

        // Execute
        submitter.execute();

        // Verify
        verify(persistencyOutputPort).accessRequestAlreadyExists(accessRequest.getIdentifier(), command.getAccessRequest().getOperation());
        verify(persistencyOutputPort).createAccessRequest(accessRequest);
        verify(executorOutputPort).submitRequest(executor.getAddress(), accessRequest);
        verify(presenter).presentAccessRequest(accessRequest);
    }

    @Test
    void testExecuteWithNullCommand() {
        // Setup
        AccessRequestSubmitter submitter = new AccessRequestSubmitter(
                presenter,
                null,
                persistencyOutputPort,
                executorOutputPort,
                transactionalOutboundPort
        );

        // Execute
        submitter.execute();

        // Verify
        verify(presenter).presentException(any(BadRequestException.class));
    }

    @Test
    void testExecuteWithNullAccessRequest() {
        // Setup
        AccessRequestSubmitterCommand command = new AccessRequestSubmitterCommand();
        command.setAccessRequest(null);

        AccessRequestSubmitter submitter = new AccessRequestSubmitter(
                presenter,
                command,
                persistencyOutputPort,
                executorOutputPort,
                transactionalOutboundPort
        );

        // Execute
        submitter.execute();

        // Verify
        verify(presenter).presentException(any(BadRequestException.class));
    }

    @Test
    void testExecuteWithNullIdentifier() {
        // Setup
        AccessRequest accessRequest = createTestAccessRequest();
        accessRequest.setIdentifier(null);

        AccessRequestSubmitterCommand command = new AccessRequestSubmitterCommand();
        command.setAccessRequest(accessRequest);

        AccessRequestSubmitter submitter = new AccessRequestSubmitter(
                presenter,
                command,
                persistencyOutputPort,
                executorOutputPort,
                transactionalOutboundPort
        );

        // Execute
        submitter.execute();

        // Verify
        verify(presenter).presentException(any(BadRequestException.class));
    }

    @Test
    void testExecuteWithNullConsumerIdentifier() {
        // Setup
        AccessRequest accessRequest = createTestAccessRequest();
        accessRequest.setConsumerIdentifier(null);

        AccessRequestSubmitterCommand command = new AccessRequestSubmitterCommand();
        command.setAccessRequest(accessRequest);

        AccessRequestSubmitter submitter = new AccessRequestSubmitter(
                presenter,
                command,
                persistencyOutputPort,
                executorOutputPort,
                transactionalOutboundPort
        );

        // Execute
        submitter.execute();

        // Verify
        verify(presenter).presentException(any(BadRequestException.class));
    }

    @Test
    void testExecuteWithExistingRequest() {
        // Setup
        AccessRequest accessRequest = createTestAccessRequest();
        AccessRequestSubmitterCommand command = new AccessRequestSubmitterCommand();
        command.setAccessRequest(accessRequest);

        AccessRequestSubmitter submitter = new AccessRequestSubmitter(
                presenter,
                command,
                persistencyOutputPort,
                executorOutputPort,
                transactionalOutboundPort
        );

        when(persistencyOutputPort.accessRequestAlreadyExists(any(), eq(command.getAccessRequest().getOperation()))).thenReturn(true);

        // Execute
        submitter.execute();

        // Verify
        verify(persistencyOutputPort).accessRequestAlreadyExists(accessRequest.getIdentifier(), command.getAccessRequest().getOperation());
        verify(persistencyOutputPort, never()).createAccessRequest(any());
        verify(executorOutputPort, never()).submitRequest(any(), any());
    }

    @Test
    void testExecuteWithExecutorClientException() {
        // Setup
        AccessRequest accessRequest = createTestAccessRequest();
        AccessRequestSubmitterCommand command = new AccessRequestSubmitterCommand();
        command.setAccessRequest(accessRequest);

        AccessRequestSubmitter submitter = new AccessRequestSubmitter(
                presenter,
                command,
                persistencyOutputPort,
                executorOutputPort,
                transactionalOutboundPort
        );

        when(persistencyOutputPort.accessRequestAlreadyExists(any(), eq(command.getAccessRequest().getOperation()))).thenReturn(false);
        when(persistencyOutputPort.createAccessRequest(any())).thenReturn(accessRequest);

        MarketplaceExecutorConfig.MarketplaceExecutor executor = new MarketplaceExecutorConfig.MarketplaceExecutor();
        executor.setName("test-executor");
        executor.setAddress("http://test-executor");
        executor.setActive(true);
        when(executorOutputPort.getExecutorsConfig()).thenReturn(Collections.singletonList(executor));
        doThrow(new ClientException(500, "Test error")).when(executorOutputPort).submitRequest(any(), any());

        // Execute
        submitter.execute();

        // Verify
        verify(persistencyOutputPort).accessRequestAlreadyExists(accessRequest.getIdentifier(), command.getAccessRequest().getOperation());
        verify(persistencyOutputPort).createAccessRequest(accessRequest);
        verify(executorOutputPort).submitRequest(executor.getAddress(), accessRequest);
        verify(presenter).presentClientException(any(ClientException.class));
    }

    @Test
    void testExecutePassesCorrectUuidToExecutor() {
        // Setup - Create a request with a user-provided UUID
        AccessRequest originalRequest = createTestAccessRequest();
        originalRequest.setUuid("user-provided-uuid-123");
        
        // Create a new request that simulates what the database would return with a generated UUID
        AccessRequest savedRequest = createTestAccessRequest();
        savedRequest.setUuid("generated-uuid-456"); // This is what the database generates
        
        AccessRequestSubmitterCommand command = new AccessRequestSubmitterCommand();
        command.setAccessRequest(originalRequest);

        AccessRequestSubmitter submitter = new AccessRequestSubmitter(
                presenter,
                command,
                persistencyOutputPort,
                executorOutputPort,
                transactionalOutboundPort
        );

        when(persistencyOutputPort.accessRequestAlreadyExists(any(), eq(command.getAccessRequest().getOperation()))).thenReturn(false);
        when(persistencyOutputPort.createAccessRequest(originalRequest)).thenReturn(savedRequest);

        MarketplaceExecutorConfig.MarketplaceExecutor executor = new MarketplaceExecutorConfig.MarketplaceExecutor();
        executor.setName("test-executor");
        executor.setAddress("http://test-executor");
        executor.setActive(true);
        when(executorOutputPort.getExecutorsConfig()).thenReturn(Collections.singletonList(executor));

        // Execute
        submitter.execute();

        // Verify that the executor receives the SAVED request (with generated UUID), not the original one
        verify(persistencyOutputPort).accessRequestAlreadyExists(originalRequest.getIdentifier(), originalRequest.getOperation());
        verify(persistencyOutputPort).createAccessRequest(originalRequest);
        verify(executorOutputPort).submitRequest(executor.getAddress(), savedRequest); // Should receive savedRequest, not originalRequest
        verify(presenter).presentAccessRequest(savedRequest);
        
        // Verify that the saved request has the generated UUID
        assertNotNull(savedRequest.getUuid());
        assertEquals("generated-uuid-456", savedRequest.getUuid());
        assertNotEquals(originalRequest.getUuid(), savedRequest.getUuid());
    }
} 