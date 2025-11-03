package org.opendatamesh.platform.pp.marketplace.accessrequests.services.submitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.client.ExecutorClient;
import org.opendatamesh.platform.pp.marketplace.configuration.executor.MarketplaceExecutorConfig;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.MarketplaceExecutorRequestRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

class AccessRequestSubmitterExecutorOutputPortImpl implements AccessRequestSubmitterExecutorOutputPort {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MarketplaceExecutorConfig executorConfig;
    private final ExecutorClient executorClient;

    public AccessRequestSubmitterExecutorOutputPortImpl(MarketplaceExecutorConfig executorConfig, ExecutorClient executorClient) {
        this.executorConfig = executorConfig;
        this.executorClient = executorClient;
    }

    @Override
    public List<MarketplaceExecutorConfig.MarketplaceExecutor> getExecutorsConfig() {
        return executorConfig.getMarketplaceExecutors();
    }

    @Override
    public void submitRequest(String executorBaseUrl, AccessRequest accessRequest) {
        MarketplaceExecutorRequestRes executorRequest = new MarketplaceExecutorRequestRes();
        executorRequest.setV("1.0");
        executorRequest.setOperation(accessRequest.getOperation().name());
        
        MarketplaceExecutorRequestRes.RequestInfo requestInfo = new MarketplaceExecutorRequestRes.RequestInfo();
        requestInfo.setName(accessRequest.getName());
        //Setting the internal uuid of the access request
        //This is done because the access request identifier is not unique
        //(the natural key is the identifier and the operation)
        requestInfo.setIdentifier(accessRequest.getUuid());
        
        // Set properties
        requestInfo.setProperties(stringToPropertiesMap(accessRequest.getProperties()));
        
        // Set provider info
        MarketplaceExecutorRequestRes.ProviderInfo providerInfo = new MarketplaceExecutorRequestRes.ProviderInfo();
        providerInfo.setDataProductFqn(accessRequest.getProviderDataProductFqn());
        providerInfo.setDataProductPortsFqn(accessRequest.getProviderDataProductPortsFqn());
        requestInfo.setProvider(providerInfo);
        
        // Set consumer info
        MarketplaceExecutorRequestRes.ConsumerInfo consumerInfo = new MarketplaceExecutorRequestRes.ConsumerInfo();
        consumerInfo.setType(accessRequest.getConsumerType());
        consumerInfo.setIdentifier(accessRequest.getConsumerIdentifier());
        requestInfo.setConsumer(consumerInfo);
        
        // Set requester info
        MarketplaceExecutorRequestRes.RequesterInfo requesterInfo = new MarketplaceExecutorRequestRes.RequesterInfo();
        requesterInfo.setType(accessRequest.getRequesterType());
        requesterInfo.setIdentifier(accessRequest.getRequesterIdentifier());
        requestInfo.setRequester(requesterInfo);
        
        // Set dates
        requestInfo.setStartDate(accessRequest.getStartDate());
        requestInfo.setEndDate(accessRequest.getEndDate());
        
        executorRequest.setRequest(requestInfo);
        executorClient.postRequest(executorBaseUrl, executorRequest);
    }

    private Map<String, Object> stringToPropertiesMap(String properties) {
        if (properties == null || properties.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(properties, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            logger.warn("Error converting string to properties map: {}", e.getMessage(), e);
        }
        // If the conversion fails, return null
        return null;
    }
} 