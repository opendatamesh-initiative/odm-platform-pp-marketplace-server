package org.opendatamesh.platform.pp.marketplace.accessrequests.services.core;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;
import org.opendatamesh.platform.pp.marketplace.accessrequests.repositories.ExecutorResponseRepository;
import org.opendatamesh.platform.pp.marketplace.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.ExecutorResponseSearchOptions;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.MarketplaceExecutorResponseMapper;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.MarketplaceExecutorResponseRes;
import org.opendatamesh.platform.pp.marketplace.utils.repositories.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.marketplace.utils.repositories.SpecsUtils;
import org.opendatamesh.platform.pp.marketplace.utils.services.GenericMappedAndFilteredCrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExecutorResponsesService extends GenericMappedAndFilteredCrudServiceImpl<ExecutorResponseSearchOptions, MarketplaceExecutorResponseRes, ExecutorResponse, Long> {

    @Autowired
    private ExecutorResponseRepository repository;

    @Autowired
    private MarketplaceExecutorResponseMapper mapper;

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<ExecutorResponse, Long> getRepository() {
        return repository;
    }

    @Override
    protected MarketplaceExecutorResponseRes toRes(ExecutorResponse entity) {
        return mapper.toResource(entity);
    }

    @Override
    protected ExecutorResponse toEntity(MarketplaceExecutorResponseRes resource) {
        return mapper.toEntity(resource);
    }

    @Override
    protected void validate(ExecutorResponse executorResponse) {
        if (executorResponse == null) {
            throw new BadRequestException("Executor response cannot be null");
        }
        if (executorResponse.getAccessRequestIdentifier() == null || executorResponse.getAccessRequestIdentifier().trim().isEmpty()) {
            throw new BadRequestException("Access request identifier is required");
        }
        if (executorResponse.getStatus() == null) {
            throw new BadRequestException("Status is required");
        }
    }

    @Override
    protected void reconcile(ExecutorResponse executorResponse) {
        // No reconciliation needed
    }

    @Override
    protected Specification<ExecutorResponse> getSpecFromFilters(ExecutorResponseSearchOptions filters) {
        List<Specification<ExecutorResponse>> specs = new ArrayList<>();
        return SpecsUtils.combineWithAnd(specs);
    }
} 