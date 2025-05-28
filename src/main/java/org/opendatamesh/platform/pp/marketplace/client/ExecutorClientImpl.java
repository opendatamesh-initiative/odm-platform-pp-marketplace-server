package org.opendatamesh.platform.pp.marketplace.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.MarketplaceExecutorRequestRes;
import org.opendatamesh.platform.pp.marketplace.utils.client.RestUtils;
import org.opendatamesh.platform.pp.marketplace.utils.client.RestUtilsFactory;
import org.springframework.web.client.RestTemplate;

public class ExecutorClientImpl implements ExecutorClient {

    private static final String EXECUTOR_REQUESTS = "api/v1/up/marketplace-executor/requests";
    private final RestUtils restUtils;

    public ExecutorClientImpl() {
        this.restUtils = RestUtilsFactory.getRestUtils(new RestTemplate());
    }

    @Override
    public void postRequest(String executorBaseUrl, MarketplaceExecutorRequestRes request) {
        restUtils.genericPost(
                String.format("%s/%s", executorBaseUrl, EXECUTOR_REQUESTS),
                null,
                request,
                JsonNode.class
        );
    }
} 