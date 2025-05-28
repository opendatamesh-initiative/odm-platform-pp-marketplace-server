package org.opendatamesh.platform.pp.marketplace.accessrequests.services.resultuploader;

import org.opendatamesh.platform.pp.marketplace.accessrequests.services.core.AccessRequestsService;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.core.ExecutorResponsesService;
import org.opendatamesh.platform.pp.marketplace.utils.usecases.DefaultTransactionalOutboundPortImpl;
import org.opendatamesh.platform.pp.marketplace.utils.usecases.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
public class AccessRequestResultUploaderFactory {

    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    private AccessRequestsService accessRequestsService;
    @Autowired
    private ExecutorResponsesService executorResponsesService;

    public UseCase buildAccessRequestResultUploader(
            AccessRequestResultUploaderCommand command,
            AccessRequestResultUploaderPresenter presenter
    ) {
        AccessRequestResultUploaderPersistencyOutputPort persistencyOutputPort = new AccessRequestResultUploaderPersistencyOutputPortImpl(
                accessRequestsService,
                executorResponsesService
        );
        return new AccessRequestResultUploader(command, presenter, persistencyOutputPort, new DefaultTransactionalOutboundPortImpl(platformTransactionManager));
    }
}
