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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ExecutorResponsesService extends GenericMappedAndFilteredCrudServiceImpl<ExecutorResponseSearchOptions, MarketplaceExecutorResponseRes, ExecutorResponse, Long> {

    @Autowired
    private ExecutorResponseRepository repository;

    @Autowired
    private MarketplaceExecutorResponseMapper mapper;

    @Autowired
    private AccessRequestsService accessRequestsService;

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
        if (executorResponse.getAccessRequest() == null || !StringUtils.hasText(executorResponse.getAccessRequest().getUuid())) {
            throw new BadRequestException("Executor Response must be associated to an access request.");
        }
        if (executorResponse.getStatus() == null) {
            throw new BadRequestException("Status is required");
        }
    }

    @Override
    protected void reconcile(ExecutorResponse executorResponse) {
        executorResponse.setAccessRequest(accessRequestsService.findOne(executorResponse.getAccessRequest().getUuid()));
        //Setting this for retro-compatibility.
        executorResponse.setAccessRequestIdentifier(executorResponse.getAccessRequest().getIdentifier());
    }

    @Override
    protected Specification<ExecutorResponse> getSpecFromFilters(ExecutorResponseSearchOptions filters) {
        List<Specification<ExecutorResponse>> specs = new ArrayList<>();
        if (StringUtils.hasText(filters.getAccessRequestIdentifier())) {
            specs.add(ExecutorResponseRepository.Specs.hasAccessRequestIdentifier(filters.getAccessRequestIdentifier()));
        }
        if (StringUtils.hasText(filters.getAccessRequestUuid())) {
            specs.add(ExecutorResponseRepository.Specs.hasAccessRequestUuid(filters.getAccessRequestUuid()));
        }
        if (StringUtils.hasText(filters.getStatus())) {
            try {
                ExecutorResponse.ExecutorResponseStatus status = ExecutorResponse.ExecutorResponseStatus.valueOf(filters.getStatus());
                specs.add(ExecutorResponseRepository.Specs.hasStatus(status));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid Executor Response Status: " + filters.getStatus() + " needs to be one of: " + Arrays.toString(ExecutorResponse.ExecutorResponseStatus.values()), e);
            }
        }
        if (!CollectionUtils.isEmpty(filters.getDataProductPortsFqn())) {
            specs.add(ExecutorResponseRepository.Specs.hasDataProductPortsFqn(filters.getDataProductPortsFqn()));
        }
        if (StringUtils.hasText(filters.getAccessRequestConsumerIdentifier())) {
            specs.add(ExecutorResponseRepository.Specs.hasAccessRequestConsumerIdentifier(filters.getAccessRequestConsumerIdentifier()));
        }
        return SpecsUtils.combineWithAnd(specs);
    }
} 