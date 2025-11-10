package org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;

@Mapper(componentModel = "spring")
public interface MarketplaceExecutorResponseMapper {

    @Mapping(target = "providerDataProductFqn", source = "provider.dataProductFqn")
    @Mapping(target = "providerDataProductPortsFqn", source = "provider.dataProductPortsFqn")
    ExecutorResponse toEntity(MarketplaceExecutorResponseRes resource);

    @Mapping(target = "providerDataProductFqn", source = "provider.dataProductFqn")
    @Mapping(target = "providerDataProductPortsFqn", source = "provider.dataProductPortsFqn")
    ExecutorResponse toEntity(MarketplaceExecutorResponseUploadRes resource);

    @Mapping(target = "provider.dataProductFqn", source = "providerDataProductFqn")
    @Mapping(target = "provider.dataProductPortsFqn", source = "providerDataProductPortsFqn")
    @Mapping(target = "accessRequestUuid", source = "accessRequest.uuid")
    @Mapping(target = "accessRequestIdentifier", source = "accessRequest.identifier")
    MarketplaceExecutorResponseRes toResource(ExecutorResponse entity);
} 