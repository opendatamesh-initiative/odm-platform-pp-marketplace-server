package org.opendatamesh.platform.pp.marketplace.accessrequests.services.resultuploader;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;

public interface AccessRequestResultUploaderPresenter {
    void presentException(RuntimeException e);

    void presentAccessRequest(AccessRequest o);

    void presentExecutorResponse(ExecutorResponse o);
}
