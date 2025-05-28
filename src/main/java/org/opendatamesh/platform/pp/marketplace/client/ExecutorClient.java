package org.opendatamesh.platform.pp.marketplace.client;

import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.MarketplaceExecutorRequestRes;

public interface ExecutorClient {
    void postRequest(String executorBaseUrl, MarketplaceExecutorRequestRes request);
}
