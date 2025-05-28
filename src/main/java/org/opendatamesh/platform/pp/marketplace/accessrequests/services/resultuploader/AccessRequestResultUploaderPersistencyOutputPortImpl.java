package org.opendatamesh.platform.pp.marketplace.accessrequests.services.resultuploader;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.core.AccessRequestsService;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.core.ExecutorResponsesService;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests.AccessRequestSearchOptions;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

class AccessRequestResultUploaderPersistencyOutputPortImpl implements AccessRequestResultUploaderPersistencyOutputPort {

    private final AccessRequestsService accessRequestsService;
    private final ExecutorResponsesService executorResponsesService;

    AccessRequestResultUploaderPersistencyOutputPortImpl(AccessRequestsService accessRequestsService, ExecutorResponsesService executorResponsesService) {
        this.accessRequestsService = accessRequestsService;
        this.executorResponsesService = executorResponsesService;
    }

    @Override
    public Optional<AccessRequest> findAccessRequest(String accessRequestIdentifier) {
        AccessRequestSearchOptions filter = new AccessRequestSearchOptions();
        filter.setIdentifier(accessRequestIdentifier);
        return accessRequestsService.findAllFiltered(Pageable.ofSize(1), filter).stream().findFirst();
    }

    @Override
    public void saveExecutorResponse(ExecutorResponse executorResponse) {
        executorResponsesService.create(executorResponse);
    }
}
