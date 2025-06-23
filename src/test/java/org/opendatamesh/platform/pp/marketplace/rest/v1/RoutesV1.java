package org.opendatamesh.platform.pp.marketplace.rest.v1;

public enum RoutesV1 {
    ACCESS_REQUESTS("/api/v1/pp/marketplace/requests"),
    EXECUTOR_RESPONSES("/api/v1/pp/marketplace/executor-responses");

    private final String path;

    RoutesV1(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
