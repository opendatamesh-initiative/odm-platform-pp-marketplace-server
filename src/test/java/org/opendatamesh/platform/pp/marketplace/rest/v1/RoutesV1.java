package org.opendatamesh.platform.pp.marketplace.rest.v1;

public enum RoutesV1 {
    ACCESS_REQUESTS("/api/v1/pp/marketplace/requests");

    private final String path;

    RoutesV1(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
