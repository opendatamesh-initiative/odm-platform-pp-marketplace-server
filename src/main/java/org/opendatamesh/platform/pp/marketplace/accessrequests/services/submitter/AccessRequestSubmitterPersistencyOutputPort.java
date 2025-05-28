package org.opendatamesh.platform.pp.marketplace.accessrequests.services.submitter;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;

interface AccessRequestSubmitterPersistencyOutputPort {
    boolean accessRequestAlreadyExists(String identifier);

    AccessRequest createAccessRequest(AccessRequest accessRequest);
}
