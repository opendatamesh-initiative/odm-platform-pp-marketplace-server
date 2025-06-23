package org.opendatamesh.platform.pp.marketplace.accessrequests.services.resultuploader;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.core.AccessRequestsService;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.core.ExecutorResponsesService;

class AccessRequestResultUploaderPersistencyOutputPortImpl implements AccessRequestResultUploaderPersistencyOutputPort {

    private final AccessRequestsService accessRequestsService;
    private final ExecutorResponsesService executorResponsesService;

    AccessRequestResultUploaderPersistencyOutputPortImpl(AccessRequestsService accessRequestsService, ExecutorResponsesService executorResponsesService) {
        this.accessRequestsService = accessRequestsService;
        this.executorResponsesService = executorResponsesService;
    }

    @Override
    public AccessRequest findAccessRequest(String accessRequestUuid) {
        return accessRequestsService.findOne(accessRequestUuid);
    }

    @Override
    public void saveExecutorResponse(ExecutorResponse executorResponse) {
        executorResponsesService.create(executorResponse);
    }
}
