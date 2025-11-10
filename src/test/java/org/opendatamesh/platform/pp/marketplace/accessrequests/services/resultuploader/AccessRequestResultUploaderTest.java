package org.opendatamesh.platform.pp.marketplace.accessrequests.services.resultuploader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;
import org.opendatamesh.platform.pp.marketplace.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.marketplace.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.marketplace.utils.usecases.TransactionalOutboundPort;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccessRequestResultUploaderTest {

    @Mock
    private AccessRequestResultUploaderPresenter presenter;
    @Mock
    private AccessRequestResultUploaderPersistencyOutputPort persistencyOutputPort;
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
        accessRequest.setName("Test Request");
        accessRequest.setOperation(AccessRequest.AccessRequestOperation.MARKETPLACE_SUBSCRIBE);
        accessRequest.setProviderDataProductFqn("test-product");
        accessRequest.setProviderDataProductPortsFqn(Arrays.asList("test-port-1", "test-port-2"));
        accessRequest.setConsumerType("test-type");
        accessRequest.setConsumerIdentifier("test-consumer-1");
        accessRequest.setStartDate(new Date());
        accessRequest.setEndDate(new Date(System.currentTimeMillis() + 86400000)); // +1 day
        return accessRequest;
    }

    private ExecutorResponse createTestExecutorResponse() {
        ExecutorResponse executorResponse = new ExecutorResponse();
        executorResponse.setStatus(ExecutorResponse.ExecutorResponseStatus.GRANTED);
        executorResponse.setMessage("Test success message");
        executorResponse.setProviderDataProductFqn("test-product");
        executorResponse.setProviderDataProductPortsFqn(Collections.singletonList("test-port-1"));
        return executorResponse;
    }

    @Test
    void testExecuteWithValidRequest() {
        // Setup
        AccessRequest accessRequest = createTestAccessRequest();
        ExecutorResponse executorResponse = createTestExecutorResponse();
        AccessRequestResultUploaderCommand command = new AccessRequestResultUploaderCommand("test-request-1", executorResponse);

        AccessRequestResultUploader uploader = new AccessRequestResultUploader(
                command,
                presenter,
                persistencyOutputPort,
                transactionalOutboundPort
        );

        when(persistencyOutputPort.findAccessRequest(any())).thenReturn(accessRequest);

        // Execute
        uploader.execute();

        // Verify
        verify(persistencyOutputPort).findAccessRequest("test-request-1");
        verify(persistencyOutputPort).saveExecutorResponse(executorResponse);
        verify(presenter).presentAccessRequest(accessRequest);
        verify(presenter).presentExecutorResponse(executorResponse);
    }

    @Test
    void testExecuteWithNullCommand() {
        // Setup
        AccessRequestResultUploader uploader = new AccessRequestResultUploader(
                null,
                presenter,
                persistencyOutputPort,
                transactionalOutboundPort
        );

        // Execute
        uploader.execute();

        // Verify
        verify(presenter).presentException(any(BadRequestException.class));
    }

    @Test
    void testExecuteWithNullExecutorResponse() {
        // Setup
        AccessRequestResultUploaderCommand command = new AccessRequestResultUploaderCommand("test-request-1", null);
        AccessRequestResultUploader uploader = new AccessRequestResultUploader(
                command,
                presenter,
                persistencyOutputPort,
                transactionalOutboundPort
        );

        // Execute
        uploader.execute();

        // Verify
        verify(presenter).presentException(any(BadRequestException.class));
    }

    @Test
    void testExecuteWithNullAccessRequestIdentifier() {
        // Setup
        ExecutorResponse executorResponse = createTestExecutorResponse();
        AccessRequestResultUploaderCommand command = new AccessRequestResultUploaderCommand(null, executorResponse);
        AccessRequestResultUploader uploader = new AccessRequestResultUploader(
                command,
                presenter,
                persistencyOutputPort,
                transactionalOutboundPort
        );

        // Execute
        uploader.execute();

        // Verify
        verify(presenter).presentException(any(BadRequestException.class));
    }

    @Test
    void testExecuteWithNonExistentAccessRequest() {
        // Setup
        ExecutorResponse executorResponse = createTestExecutorResponse();
        AccessRequestResultUploaderCommand command = new AccessRequestResultUploaderCommand("test-request-1", executorResponse);
        AccessRequestResultUploader uploader = new AccessRequestResultUploader(
                command,
                presenter,
                persistencyOutputPort,
                transactionalOutboundPort
        );

        when(persistencyOutputPort.findAccessRequest(any())).thenThrow(new NotFoundException("Test exception"));

        // Execute
        uploader.execute();

        // Verify
        verify(persistencyOutputPort).findAccessRequest("test-request-1");
        verify(presenter).presentException(any(NotFoundException.class));
    }

    @Test
    void testExecuteWithMismatchedProviderFqn() {
        // Setup
        AccessRequest accessRequest = createTestAccessRequest();
        ExecutorResponse executorResponse = createTestExecutorResponse();
        executorResponse.setProviderDataProductFqn("different-product");

        AccessRequestResultUploaderCommand command = new AccessRequestResultUploaderCommand("test-request-1", executorResponse);
        AccessRequestResultUploader uploader = new AccessRequestResultUploader(
                command,
                presenter,
                persistencyOutputPort,
                transactionalOutboundPort
        );

        when(persistencyOutputPort.findAccessRequest(any())).thenReturn(accessRequest);

        // Execute
        uploader.execute();

        // Verify
        verify(persistencyOutputPort).findAccessRequest("test-request-1");
        verify(presenter).presentException(any(BadRequestException.class));
    }

    @Test
    void testExecuteWithInvalidPortFqn() {
        // Setup
        AccessRequest accessRequest = createTestAccessRequest();
        ExecutorResponse executorResponse = createTestExecutorResponse();
        executorResponse.setProviderDataProductPortsFqn(Arrays.asList("invalid-port", "test-port-1"));

        AccessRequestResultUploaderCommand command = new AccessRequestResultUploaderCommand("test-request-1", executorResponse);
        AccessRequestResultUploader uploader = new AccessRequestResultUploader(
                command,
                presenter,
                persistencyOutputPort,
                transactionalOutboundPort
        );

        when(persistencyOutputPort.findAccessRequest(any())).thenReturn(accessRequest);

        // Execute
        uploader.execute();

        // Verify
        verify(persistencyOutputPort).findAccessRequest("test-request-1");
        verify(presenter).presentException(any(BadRequestException.class));
    }
} 