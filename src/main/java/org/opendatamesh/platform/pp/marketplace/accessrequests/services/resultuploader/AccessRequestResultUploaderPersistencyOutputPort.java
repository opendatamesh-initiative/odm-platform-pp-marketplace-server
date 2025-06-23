package org.opendatamesh.platform.pp.marketplace.accessrequests.services.resultuploader;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;

interface AccessRequestResultUploaderPersistencyOutputPort {

    AccessRequest findAccessRequest(String accessRequestUuid);

    void saveExecutorResponse(ExecutorResponse executorResponse);

}
