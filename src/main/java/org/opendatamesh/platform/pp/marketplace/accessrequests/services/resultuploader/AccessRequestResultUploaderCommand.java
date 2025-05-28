package org.opendatamesh.platform.pp.marketplace.accessrequests.services.resultuploader;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;

public class AccessRequestResultUploaderCommand {
    private final String accessRequestIdentifier;
    private final ExecutorResponse executorResponse;

    public AccessRequestResultUploaderCommand(String accessRequestIdentifier, ExecutorResponse executorResponse) {
        this.accessRequestIdentifier = accessRequestIdentifier;
        this.executorResponse = executorResponse;
    }

    public ExecutorResponse getExecutorResponse() {
        return executorResponse;
    }

    public String getAccessRequestIdentifier() {
        return accessRequestIdentifier;
    }
}
