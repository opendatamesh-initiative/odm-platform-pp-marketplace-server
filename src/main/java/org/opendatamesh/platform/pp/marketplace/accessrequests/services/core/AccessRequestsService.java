package org.opendatamesh.platform.pp.marketplace.accessrequests.services.core;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.accessrequests.repositories.AccessRequestRepository;
import org.opendatamesh.platform.pp.marketplace.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests.AccessRequestMapper;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests.AccessRequestRes;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests.AccessRequestSearchOptions;
import org.opendatamesh.platform.pp.marketplace.utils.repositories.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.marketplace.utils.repositories.SpecsUtils;
import org.opendatamesh.platform.pp.marketplace.utils.services.GenericMappedAndFilteredCrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccessRequestsService extends GenericMappedAndFilteredCrudServiceImpl<AccessRequestSearchOptions, AccessRequestRes, AccessRequest, String> {

    @Autowired
    private AccessRequestRepository repository;

    @Autowired
    private AccessRequestMapper mapper;

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<AccessRequest, String> getRepository() {
        return repository;
    }

    @Override
    protected AccessRequestRes toRes(AccessRequest entity) {
        return mapper.toResource(entity);
    }

    @Override
    protected AccessRequest toEntity(AccessRequestRes resource) {
        return mapper.toEntity(resource);
    }

    @Override
    protected void validate(AccessRequest accessRequest) {
        if (accessRequest == null) {
            throw new BadRequestException("Access request cannot be null");
        }

        if (!StringUtils.hasText(accessRequest.getName())) {
            throw new BadRequestException("Name is required");
        }

        if (!StringUtils.hasText(accessRequest.getIdentifier())) {
            throw new BadRequestException("Identifier is required");
        }

        if (accessRequest.getOperation() == null) {
            throw new BadRequestException("Operation is required");
        }

        if (accessRequest.getConsumerType() == null) {
            throw new BadRequestException("Consumer type is required");
        }

        if (!StringUtils.hasText(accessRequest.getConsumerIdentifier())) {
            throw new BadRequestException("Consumer identifier is required");
        }

        if (!StringUtils.hasText(accessRequest.getProviderDataProductFqn())) {
            throw new BadRequestException("Provider data product FQN is required");
        }

        if (CollectionUtils.isEmpty(accessRequest.getProviderDataProductPortsFqn())) {
            throw new BadRequestException("At least one provider data product port FQN is required");
        }

        if (accessRequest.getStartDate() == null) {
            throw new BadRequestException("Start date is required");
        }

        if (accessRequest.getEndDate() != null && !accessRequest.getStartDate().before(accessRequest.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }
    }

    @Override
    protected void reconcile(AccessRequest objectToReconcile) {
        //DO NOTHING
    }

    @Override
    protected Specification<AccessRequest> getSpecFromFilters(AccessRequestSearchOptions filters) {
        List<Specification<AccessRequest>> specs = new ArrayList<>();
        if(StringUtils.hasText(filters.getIdentifier())){
            specs.add(AccessRequestRepository.Specs.hasIdentifier(filters.getIdentifier()));
        }
        if(filters.getOperation() != null){
            specs.add(AccessRequestRepository.Specs.hasOperation(filters.getOperation()));
        }
        return SpecsUtils.combineWithAnd(specs);
    }
}
