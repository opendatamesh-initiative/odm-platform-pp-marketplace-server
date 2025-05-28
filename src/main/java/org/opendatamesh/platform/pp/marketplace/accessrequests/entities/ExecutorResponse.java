package org.opendatamesh.platform.pp.marketplace.accessrequests.entities;

import org.opendatamesh.platform.pp.marketplace.utils.entities.VersionedEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "marketplace_access_requests_executor_responses")
public class ExecutorResponse extends VersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "access_request_uuid")
    private AccessRequest accessRequest;

    @Column(name = "access_request_identifier", nullable = false)
    private String accessRequestIdentifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExecutorResponseStatus status;

    @Column(name = "message")
    private String message;

    @Column(name = "provider_data_product_fqn", nullable = false)
    private String providerDataProductFqn;

    @ElementCollection
    @CollectionTable(
            name = "marketplace_access_requests_executor_responses_ports",
            joinColumns = @JoinColumn(name = "executor_response_id")
    )
    @Column(name = "port_fqn")
    private List<String> providerDataProductPortsFqn = new ArrayList<>();

    public ExecutorResponse() {
        //DO NOTHING
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public AccessRequest getAccessRequest() {
        return accessRequest;
    }

    public void setAccessRequest(AccessRequest accessRequest) {
        this.accessRequest = accessRequest;
        if (accessRequest != null) {
            accessRequestIdentifier = accessRequest.getIdentifier();
        }
    }

    public enum ExecutorResponseStatus {
        PENDING,
        GRANTED,
        DENIED,
        REVOKED,
        ERROR
    }
} 