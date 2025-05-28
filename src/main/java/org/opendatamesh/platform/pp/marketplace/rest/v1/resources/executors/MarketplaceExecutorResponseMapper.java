package org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;

@Mapper(componentModel = "spring")
public interface MarketplaceExecutorResponseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accessRequest", ignore = true)
    @Mapping(target = "providerDataProductFqn", source = "provider.dataProductFqn")
    @Mapping(target = "providerDataProductPortsFqn", source = "provider.dataProductPortsFqn")
    ExecutorResponse toEntity(MarketplaceExecutorResponseRes resource);

    @Mapping(target = "provider.dataProductFqn", source = "providerDataProductFqn")
    @Mapping(target = "provider.dataProductPortsFqn", source = "providerDataProductPortsFqn")
    MarketplaceExecutorResponseRes toResource(ExecutorResponse entity);
} 