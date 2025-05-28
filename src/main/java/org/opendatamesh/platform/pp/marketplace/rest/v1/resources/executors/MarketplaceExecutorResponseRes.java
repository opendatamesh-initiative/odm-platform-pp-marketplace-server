package org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors;

import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.pp.marketplace.utils.resources.VersionedRes;

import java.util.List;

@Schema(description = "Marketplace Executor Response Resource")
public class MarketplaceExecutorResponseRes extends VersionedRes {

    @Schema(description = "Identifier of the access request this response is for", example = "AR-123")
    private String accessRequestIdentifier;
    @Schema(description = "Status of the executor response", example = "SUCCESS")
    private ExecutorResponseStatus status;
    @Schema(description = "Message from the executor", example = "Access request processed successfully")
    private String message;
    @Schema(description = "Provider information")
    private ProviderInfo provider;

    public MarketplaceExecutorResponseRes() {
        //DO NOTHING
    }


    public String getAccessRequestIdentifier() {
        return accessRequestIdentifier;
    }

    public void setAccessRequestIdentifier(String accessRequestIdentifier) {
        this.accessRequestIdentifier = accessRequestIdentifier;
    }

    public ExecutorResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutorResponseStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProviderInfo getProvider() {
        return provider;
    }

    public void setProvider(ProviderInfo provider) {
        this.provider = provider;
    }

    public enum ExecutorResponseStatus {
        PENDING,
        GRANTED,
        DENIED,
        REVOKED,
        ERROR
    }

    @Schema(description = "Provider information for the request")
    public static class ProviderInfo {
        @Schema(description = "Data product fully qualified name")
        private String dataProductFqn;

        @Schema(description = "Data product ports fully qualified names")
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