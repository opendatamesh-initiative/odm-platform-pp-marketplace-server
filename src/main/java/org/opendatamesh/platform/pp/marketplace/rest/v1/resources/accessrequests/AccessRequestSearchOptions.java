package org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;

public class AccessRequestSearchOptions {
    private String identifier;
    private AccessRequest.AccessRequestOperation operation;

    public AccessRequestSearchOptions() {
        //DO NOTHING
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public AccessRequest.AccessRequestOperation getOperation() {
        return operation;
    }

    public void setOperation(AccessRequest.AccessRequestOperation operation) {
        this.operation = operation;
    }
}