package org.opendatamesh.platform.pp.marketplace.accessrequests.services.submitter;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;

public class AccessRequestSubmitterCommand {
    private AccessRequest accessRequest;

    public AccessRequest getAccessRequest() {
        return accessRequest;
    }

    public void setAccessRequest(AccessRequest accessRequest) {
        this.accessRequest = accessRequest;
    }
}
