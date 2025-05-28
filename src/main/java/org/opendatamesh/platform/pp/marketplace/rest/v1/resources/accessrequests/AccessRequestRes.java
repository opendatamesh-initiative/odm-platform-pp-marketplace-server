package org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.pp.marketplace.utils.resources.VersionedRes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Schema(description = "Access Request Resource")
public class AccessRequestRes extends VersionedRes {

    @Schema(description = "Unique identifier of the access request", example = "123e4567-e89b-12d3-a456-426614174000")
    private String uuid;
    @Schema(description = "Identifier of the access request", example = "AR-123")
    private String identifier;
    @Schema(description = "Name of the access request", example = "My Access Request")
    private String name;
    @Schema(description = "Operation type of the access request. Can be either MARKETPLACE_SUBSCRIBE or MARKETPLACE_UNSUBSCRIBE", example = "MARKETPLACE_SUBSCRIBE")
    private AccessRequestOperationRes operation;
    @Schema(description = "Type of the requester", example = "USER")
    private String requesterType;
    @Schema(description = " Identifier of the requester", example = "user123")
    private String requesterIdentifier;
    @Schema(description = "Type of consumer requesting access. Can be USER, TEAM, or DATA_PRODUCT", example = "USER")
    private String consumerType;
    @Schema(description = "Identifier of the consumer requesting access", example = "user456")
    private String consumerIdentifier;
    @Schema(description = "Fully qualified name of the provider data product", example = "org.example:product:1.0.0")
    private String providerDataProductFqn;
    @Schema(description = "List of fully qualified names of provider data product ports. At least one port must be specified",
            example = "[\"org.example:product:1.0.0:port1\", \"org.example:product:1.0.0:port2\"]")
    private List<String> providerDataProductPortsFqn = new ArrayList<>();
    @Schema(description = "Additional properties of the access request in JSON format", example = "{\"purpose\": \"Data analysis\", \"duration\": \"1 year\"}")
    private ObjectNode properties;
    @Schema(description = "Start date of the access period. Must be a valid date and must be before the end date",
            example = "2024-01-01T00:00:00Z")
    private Date startDate;
    @Schema(description = "End date of the access period. Must be a valid date and must be after the start date",
            example = "2024-12-31T23:59:59Z")
    private Date endDate;

    public AccessRequestRes() {
        //DO NOTHING
    }

    public enum AccessRequestOperationRes {
        MARKETPLACE_SUBSCRIBE,
        MARKETPLACE_UNSUBSCRIBE
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccessRequestOperationRes getOperation() {
        return operation;
    }

    public void setOperation(AccessRequestOperationRes operation) {
        this.operation = operation;
    }

    public String getRequesterType() {
        return requesterType;
    }

    public void setRequesterType(String requesterType) {
        this.requesterType = requesterType;
    }

    public String getRequesterIdentifier() {
        return requesterIdentifier;
    }

    public void setRequesterIdentifier(String requesterIdentifier) {
        this.requesterIdentifier = requesterIdentifier;
    }

    public String getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(String consumerType) {
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
}


