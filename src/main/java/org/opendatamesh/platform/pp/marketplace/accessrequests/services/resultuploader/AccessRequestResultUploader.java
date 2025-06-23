package org.opendatamesh.platform.pp.marketplace.accessrequests.services.resultuploader;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;
import org.opendatamesh.platform.pp.marketplace.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.marketplace.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.marketplace.utils.usecases.TransactionalOutboundPort;
import org.opendatamesh.platform.pp.marketplace.utils.usecases.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

class AccessRequestResultUploader implements UseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AccessRequestResultUploaderCommand command;
    private final AccessRequestResultUploaderPresenter presenter;
    private final AccessRequestResultUploaderPersistencyOutputPort persistencyOutputPort;
    private final TransactionalOutboundPort transactionalOutboundPort;

    public AccessRequestResultUploader(
            AccessRequestResultUploaderCommand command,
            AccessRequestResultUploaderPresenter presenter,
            AccessRequestResultUploaderPersistencyOutputPort persistencyOutputPort,
            TransactionalOutboundPort transactionalOutboundPort) {
        this.command = command;
        this.presenter = presenter;
        this.persistencyOutputPort = persistencyOutputPort;
        this.transactionalOutboundPort = transactionalOutboundPort;
    }

    @Override
    public void execute() {
        try {
            validateCommand();
            transactionalOutboundPort.doInTransaction(() -> {
                AccessRequest accessRequest = persistencyOutputPort.findAccessRequest(command.getAccessRequestUuid());

                ExecutorResponse executorResponse = command.getExecutorResponse();
                executorResponse.setAccessRequest(accessRequest);
                verifyResponseContainsCorrectProviderFqns(accessRequest, executorResponse);

                persistencyOutputPort.saveExecutorResponse(executorResponse);

                presenter.presentAccessRequest(accessRequest);
                presenter.presentExecutorResponse(executorResponse);
            });
        } catch (NotFoundException | BadRequestException e) {
            presenter.presentException(e);
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            presenter.presentException(e);
        }
    }

    private void verifyResponseContainsCorrectProviderFqns(AccessRequest accessRequest, ExecutorResponse executorResponse) {
        // Verify that the provider data product FQN matches
        if (!accessRequest.getProviderDataProductFqn().equals(executorResponse.getProviderDataProductFqn())) {
            throw new BadRequestException("Executor response provider data product FQN does not match the original access request");
        }

        // Verify that the provider data product ports FQNs are a subset of the original request
        if (!new HashSet<>(accessRequest.getProviderDataProductPortsFqn()).containsAll(executorResponse.getProviderDataProductPortsFqn())) {
            throw new BadRequestException("Executor response provider data product ports FQNs must be a subset of the original access request ports");
        }
    }

    private void validateCommand() {
        if (command == null) {
            throw new BadRequestException("Command cannot be null");
        }
        if (command.getExecutorResponse() == null) {
            throw new BadRequestException("Executor response cannot be null");
        }
        if (command.getAccessRequestUuid() == null) {
            throw new BadRequestException("Access request identifier cannot be null");
        }

    }
}
