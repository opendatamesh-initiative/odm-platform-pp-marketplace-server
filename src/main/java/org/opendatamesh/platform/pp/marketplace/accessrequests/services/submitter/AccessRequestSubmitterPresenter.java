package org.opendatamesh.platform.pp.marketplace.accessrequests.services.submitter;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;

public interface AccessRequestSubmitterPresenter {
    void presentException(RuntimeException e);

    void presentClientException(RuntimeException e);

    void presentAccessRequest(AccessRequest accessRequest);

}
