package org.opendatamesh.platform.pp.marketplace.accessrequests.services.submitter;

import org.apache.commons.lang3.StringUtils;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.configuration.executor.MarketplaceExecutorConfig.MarketplaceExecutor;
import org.opendatamesh.platform.pp.marketplace.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.marketplace.exceptions.InternalException;
import org.opendatamesh.platform.pp.marketplace.exceptions.client.ClientException;
import org.opendatamesh.platform.pp.marketplace.exceptions.client.ClientResourceMappingException;
import org.opendatamesh.platform.pp.marketplace.utils.usecases.TransactionalOutboundPort;
import org.opendatamesh.platform.pp.marketplace.utils.usecases.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

class AccessRequestSubmitter implements UseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AccessRequestSubmitterPresenter presenter;
    private final AccessRequestSubmitterCommand command;
    private final AccessRequestSubmitterPersistencyOutputPort persistencyOutputPort;
    private final AccessRequestSubmitterExecutorOutputPort executorOutputPort;
    private final TransactionalOutboundPort transactionalOutboundPort;

    AccessRequestSubmitter(
            AccessRequestSubmitterPresenter presenter,
            AccessRequestSubmitterCommand command,
            AccessRequestSubmitterPersistencyOutputPort persistencyOutputPort,
            AccessRequestSubmitterExecutorOutputPort executorOutputPort,
            TransactionalOutboundPort transactionalOutboundPort) {
        this.presenter = presenter;
        this.command = command;
        this.persistencyOutputPort = persistencyOutputPort;
        this.executorOutputPort = executorOutputPort;
        this.transactionalOutboundPort = transactionalOutboundPort;
    }

    @Override
    public void execute() {
        try {
            validateCommand();
            transactionalOutboundPort.doInTransaction(() -> {
                if (persistencyOutputPort.accessRequestAlreadyExists(command.getAccessRequest().getIdentifier())) {
                    throw new BadRequestException("Access request already exists with identifier: " + command.getAccessRequest().getIdentifier());
                }

                AccessRequest accessRequest = persistencyOutputPort.createAccessRequest(command.getAccessRequest());
                AtomicBoolean requestHandled = new AtomicBoolean(false);
                for (MarketplaceExecutor executor : executorOutputPort.getExecutorsConfig()) {
                    if (executor.isActive()) {
                        submitRequestToExecutor(executor, requestHandled);
                    }
                }
                if (!requestHandled.get()) {
                    //Rollback the transaction
                    throw new InternalException("No executor was able to handle the request with identifier: " + command.getAccessRequest().getIdentifier());
                }
                presenter.presentAccessRequest(accessRequest);
            });
        } catch (BadRequestException e) {
            presenter.presentException(e);
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            presenter.presentException(e);
        }
    }

    private void validateCommand() {
        if (command == null) {
            throw new BadRequestException("Command cannot be null");
        }
        if (command.getAccessRequest() == null) {
            throw new BadRequestException("Access request cannot be null");
        }
        if (StringUtils.isEmpty(command.getAccessRequest().getIdentifier())) {
            throw new BadRequestException("Access request identifier cannot be null or empty");
        }
        if (StringUtils.isEmpty(command.getAccessRequest().getConsumerIdentifier())) {
            throw new BadRequestException("Consumer identifier cannot be null or empty");
        }
    }

    private void submitRequestToExecutor(MarketplaceExecutor executor, AtomicBoolean requestHandled) {
        try {
            executorOutputPort.submitRequest(executor.getAddress(), command.getAccessRequest());
            requestHandled.set(true);
            logger.info("Request {} handled by executor: {}", command.getAccessRequest().getIdentifier(), executor.getName());
        } catch (ClientException | ClientResourceMappingException e) {
            logger.warn(e.getMessage(), e);
            presenter.presentClientException(e);
        }
    }

}
