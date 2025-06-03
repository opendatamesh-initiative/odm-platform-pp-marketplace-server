package org.opendatamesh.platform.pp.marketplace.rest.v1.resources.events;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.pp.marketplace.utils.resources.VersionedRes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExecutorResultReceivedEvent {
    private final String version = "1.0.0";
    private final String type = "MARKETPLACE_EXECUTOR_RESULT_RECEIVED";

    private ExecutorResultReceivedEventState beforeState;
    private ExecutorResultReceivedEventState afterState;

    public ExecutorResultReceivedEvent() {
    }

    public ExecutorResultReceivedEvent(ExecutorResultReceivedEventState beforeState, ExecutorResultReceivedEventState afterState) {
        this.beforeState = beforeState;
        this.afterState = afterState;
    }

    public String getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public ExecutorResultReceivedEventState getBeforeState() {
        return beforeState;
    }

    public void setBeforeState(ExecutorResultReceivedEventState beforeState) {
        this.beforeState = beforeState;
    }

    public ExecutorResultReceivedEventState getAfterState() {
        return afterState;
    }

    public void setAfterState(ExecutorResultReceivedEventState afterState) {
        this.afterState = afterState;
    }

    public static class ExecutorResultReceivedEventState {
        private ExecutorResultReceivedEventAccessRequest accessRequest;
        private ExecutorResultReceivedEventExecutorResponse executorResponse;

        public ExecutorResultReceivedEventState() {
        }

        public ExecutorResultReceivedEventState(ExecutorResultReceivedEventAccessRequest accessRequest, ExecutorResultReceivedEventExecutorResponse executorResponse) {
            this.accessRequest = accessRequest;
            this.executorResponse = executorResponse;
        }

        public ExecutorResultReceivedEventAccessRequest getAccessRequest() {
            return accessRequest;
        }

        public void setAccessRequest(ExecutorResultReceivedEventAccessRequest accessRequest) {
            this.accessRequest = accessRequest;
        }

        public ExecutorResultReceivedEventExecutorResponse getExecutorResponse() {
            return executorResponse;
        }

        public void setExecutorResponse(ExecutorResultReceivedEventExecutorResponse executorResponse) {
            this.executorResponse = executorResponse;
        }
    }

    public static class ExecutorResultReceivedEventAccessRequest extends VersionedRes {
        private String uuid;
        private String name;
        private String identifier;
        private ExecutorResultReceivedEventAccessRequestOperation operation;
        private String reviewerIdentifier;
        private ExecutorResultReceivedEventAccessRequestConsumerType consumerType;
        private String consumerIdentifier;
        private String providerDataProductFqn;
        private List<String> providerDataProductPortsFqn = new ArrayList<>();
        private ObjectNode properties;
        private Date startDate;
        private Date endDate;

        public ExecutorResultReceivedEventAccessRequest() {
            //DO NOTHING
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public ExecutorResultReceivedEventAccessRequestOperation getOperation() {
            return operation;
        }

        public void setOperation(ExecutorResultReceivedEventAccessRequestOperation operation) {
            this.operation = operation;
        }

        public String getReviewerIdentifier() {
            return reviewerIdentifier;
        }

        public void setReviewerIdentifier(String reviewerIdentifier) {
            this.reviewerIdentifier = reviewerIdentifier;
        }

        public ExecutorResultReceivedEventAccessRequestConsumerType getConsumerType() {
            return consumerType;
        }

        public void setConsumerType(ExecutorResultReceivedEventAccessRequestConsumerType consumerType) {
            this.consumerType = consumerType;
        }

        public String getConsumerIdentifier() {
            return consumerIdentifier;
        }

        public void setConsumerIdentifier(String consumerIdentifier) {
            this.consumerIdentifier = consumerIdentifier;
        }

        public String getProviderDataProductFqn() {
            return providerDataProductFqn;
        }

        public void setProviderDataProductFqn(String providerDataProductFqn) {
            this.providerDataProductFqn = providerDataProductFqn;
        }

        public List<String> getProviderDataProductPortsFqn() {
            return providerDataProductPortsFqn;
        }

        public void setProviderDataProductPortsFqn(List<String> providerDataProductPortsFqn) {
            this.providerDataProductPortsFqn = providerDataProductPortsFqn;
        }

        public ObjectNode getProperties() {
            return properties;
        }

        public void setProperties(ObjectNode properties) {
            this.properties = properties;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public enum ExecutorResultReceivedEventAccessRequestOperation {
            MARKETPLACE_SUBSCRIBE,
            MARKETPLACE_UNSUBSCRIBE
        }

        public enum ExecutorResultReceivedEventAccessRequestConsumerType {
            USER,
            TEAM,
            DATA_PRODUCT
        }
    }

    public static class ExecutorResultReceivedEventExecutorResponse extends VersionedRes {
        private String accessRequestIdentifier;
        private ExecutorResultReceivedEventExecutorResponseStatus status;
        private String message;
        private ExecutorResultReceivedEventProviderInfo provider;

        public ExecutorResultReceivedEventExecutorResponse() {
            //DO NOTHING
        }

        public String getAccessRequestIdentifier() {
            return accessRequestIdentifier;
        }

        public void setAccessRequestIdentifier(String accessRequestIdentifier) {
            this.accessRequestIdentifier = accessRequestIdentifier;
        }

        public ExecutorResultReceivedEventExecutorResponseStatus getStatus() {
            return status;
        }

        public void setStatus(ExecutorResultReceivedEventExecutorResponseStatus status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public enum ExecutorResultReceivedEventExecutorResponseStatus {
            PENDING,
            GRANTED,
            DENIED,
            REVOKED,
            ERROR
        }

        public ExecutorResultReceivedEventProviderInfo getProvider() {
            return provider;
        }

        public void setProvider(ExecutorResultReceivedEventProviderInfo provider) {
            this.provider = provider;
        }
    }

    public static class ExecutorResultReceivedEventProviderInfo {

        private String dataProductFqn;
        private List<String> dataProductPortsFqn;

        public String getDataProductFqn() {
            return dataProductFqn;
        }

        public void setDataProductFqn(String dataProductFqn) {
            this.dataProductFqn = dataProductFqn;
        }

        public List<String> getDataProductPortsFqn() {
            return dataProductPortsFqn;
        }

        public void setDataProductPortsFqn(List<String> dataProductPortsFqn) {
            this.dataProductPortsFqn = dataProductPortsFqn;
        }
    }
} 