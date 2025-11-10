package org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Search options for filtering executor responses")
public class ExecutorResponseSearchOptions {
    @Schema(description = "Filter by access request identifier", example = "AR-123")
    private String accessRequestIdentifier;

    @Schema(description = "Filter by access request UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String accessRequestUuid;

    @Schema(description = "Filter by executor response status", example = "GRANTED",
            allowableValues = {"PENDING", "GRANTED", "DENIED", "REVOKED", "ERROR"})
    private String status;

    @Schema(description = "Filter by data product ports fully qualified names. Returns responses where any of the provided ports match.",
            example = "[\"port1.fqn\", \"port2.fqn\"]")
    private List<String> dataProductPortsFqn;

    @Schema(description = "Filter by the consumer identifier of the associated access request", example = "consumer-123")
    private String accessRequestConsumerIdentifier;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getDataProductPortsFqn() {
        return dataProductPortsFqn;
    }

    public void setDataProductPortsFqn(List<String> dataProductPortsFqn) {
        this.dataProductPortsFqn = dataProductPortsFqn;
    }

    public String getAccessRequestConsumerIdentifier() {
        return accessRequestConsumerIdentifier;
    }

    public void setAccessRequestConsumerIdentifier(String accessRequestConsumerIdentifier) {
        this.accessRequestConsumerIdentifier = accessRequestConsumerIdentifier;
    }
} 