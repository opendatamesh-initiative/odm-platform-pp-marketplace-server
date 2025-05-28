package org.opendatamesh.platform.pp.marketplace.accessrequests.services.resultuploader;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;

import java.util.Optional;

interface AccessRequestResultUploaderPersistencyOutputPort {

    Optional<AccessRequest> findAccessRequest(String accessRequestIdentifier);

    void saveExecutorResponse(ExecutorResponse executorResponse);

}
