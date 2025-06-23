package org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors;

public class ExecutorResponseSearchOptions {
    private String accessRequestIdentifier;
    private String accessRequestUuid;

    public ExecutorResponseSearchOptions() {
        //DO NOTHING
    }

    public String getAccessRequestIdentifier() {
        return accessRequestIdentifier;
    }

    public void setAccessRequestIdentifier(String accessRequestIdentifier) {
        this.accessRequestIdentifier = accessRequestIdentifier;
    }

    public String getAccessRequestUuid() {
        return accessRequestUuid;
    }

    public void setAccessRequestUuid(String accessRequestUuid) {
        this.accessRequestUuid = accessRequestUuid;
    }
} 