package org.opendatamesh.platform.pp.marketplace.accessrequests.services.submitter;

import org.opendatamesh.platform.pp.marketplace.accessrequests.services.core.AccessRequestsService;
import org.opendatamesh.platform.pp.marketplace.client.ExecutorClient;
import org.opendatamesh.platform.pp.marketplace.configuration.executor.MarketplaceExecutorConfig;
import org.opendatamesh.platform.pp.marketplace.utils.usecases.DefaultTransactionalOutboundPortImpl;
import org.opendatamesh.platform.pp.marketplace.utils.usecases.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
public class AccessRequestSubmitterFactory {

    private final MarketplaceExecutorConfig marketplaceExecutorConfig;
    private final ExecutorClient executorClient;
    private final PlatformTransactionManager transactionManager;
    private final AccessRequestsService accessRequestsService;

    @Autowired
    public AccessRequestSubmitterFactory(
            MarketplaceExecutorConfig marketplaceExecutorConfig,
            ExecutorClient executorClient,
            PlatformTransactionManager transactionManager,
            AccessRequestsService accessRequestsService) {
        this.marketplaceExecutorConfig = marketplaceExecutorConfig;
        this.executorClient = executorClient;
        this.transactionManager = transactionManager;
        this.accessRequestsService = accessRequestsService;
    }

    public UseCase buildAccessRequestSubmitter(AccessRequestSubmitterCommand command, AccessRequestSubmitterPresenter presenter) {
        AccessRequestSubmitterExecutorOutputPort executorOutputPort = new AccessRequestSubmitterExecutorOutputPortImpl(marketplaceExecutorConfig, executorClient);
        AccessRequestSubmitterPersistencyOutputPort persistencyOutputPort = new AccessRequestSubmitterPersistencyOutputPortImpl(accessRequestsService);
        return new AccessRequestSubmitter(
                presenter,
                command,
                persistencyOutputPort,
                executorOutputPort,
                new DefaultTransactionalOutboundPortImpl(transactionManager)
        );
    }
} 