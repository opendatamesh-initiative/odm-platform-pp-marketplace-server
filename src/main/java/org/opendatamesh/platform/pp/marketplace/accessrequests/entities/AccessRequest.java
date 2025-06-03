package org.opendatamesh.platform.pp.marketplace.accessrequests.entities;

import org.hibernate.annotations.GenericGenerator;
import org.opendatamesh.platform.pp.marketplace.utils.entities.VersionedEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "marketplace_access_requests")
public class AccessRequest extends VersionedEntity {

    @Id
    @Column(name = "uuid")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "identifier")
    private String identifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation")
    private AccessRequestOperation operation;

    @Column(name = "requester_type")
    private String requesterType;

    @Column(name = "requester_identifier")
    private String requesterIdentifier;

    @Column(name = "reviewer_identifier")
    private String reviewerIdentifier;

    @Column(name = "consumer_type")
    private String consumerType;

    @Column(name = "consumer_identifier")
    private String consumerIdentifier;

    @Column(name = "provider_data_product_fqn")
    private String providerDataProductFqn;

    @ElementCollection
    @CollectionTable(
            name = "marketplace_access_requests_ports",
            joinColumns = @JoinColumn(name = "access_request_uuid")
    )
    @Column(name = "port_fqn")
    private List<String> providerDataProductPortsFqn = new ArrayList<>();

    @Column(name = "properties")
    private String properties;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    public AccessRequest() {
        //DO NOTHING
    }

    public enum AccessRequestOperation {
        MARKETPLACE_SUBSCRIBE,
        MARKETPLACE_UNSUBSCRIBE
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

    public AccessRequestOperation getOperation() {
        return operation;
    }

    public void setOperation(AccessRequestOperation operation) {
        this.operation = operation;
    }

    public String getReviewerIdentifier() {
        return reviewerIdentifier;
    }

    public void setReviewerIdentifier(String reviewerIdentifier) {
        this.reviewerIdentifier = reviewerIdentifier;
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

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
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
}
