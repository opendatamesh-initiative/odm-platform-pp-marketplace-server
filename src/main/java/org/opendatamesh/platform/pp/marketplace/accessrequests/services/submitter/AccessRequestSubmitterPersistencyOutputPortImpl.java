package org.opendatamesh.platform.pp.marketplace.accessrequests.services.submitter;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.core.AccessRequestsService;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests.AccessRequestSearchOptions;
import org.springframework.data.domain.Pageable;

class AccessRequestSubmitterPersistencyOutputPortImpl implements AccessRequestSubmitterPersistencyOutputPort {
    private final AccessRequestsService accessRequestsService;

    AccessRequestSubmitterPersistencyOutputPortImpl(AccessRequestsService accessRequestsService) {
        this.accessRequestsService = accessRequestsService;
    }

    @Override
    public boolean accessRequestAlreadyExists(String identifier) {
        AccessRequestSearchOptions filter = new AccessRequestSearchOptions();
        filter.setIdentifier(identifier);
        return accessRequestsService.findAllFiltered(Pageable.ofSize(1), filter).hasContent();
    }

    @Override
    public AccessRequest createAccessRequest(AccessRequest accessRequest) {
        return accessRequestsService.create(accessRequest);
    }
}
