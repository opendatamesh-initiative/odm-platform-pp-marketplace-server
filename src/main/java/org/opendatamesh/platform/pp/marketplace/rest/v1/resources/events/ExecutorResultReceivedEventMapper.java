package org.opendatamesh.platform.pp.marketplace.rest.v1.resources.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;

@Mapper(componentModel = "spring")
public interface ExecutorResultReceivedEventMapper {

    @Mapping(target = "properties", expression = "java(stringToProperties(entity.getProperties()))")
    ExecutorResultReceivedEvent.ExecutorResultReceivedEventAccessRequest toEventAccessRequest(AccessRequest entity);

    @Mapping(target = "provider.dataProductFqn", source = "providerDataProductFqn")
    @Mapping(target = "provider.dataProductPortsFqn", source = "providerDataProductPortsFqn")
    @Mapping(target = "accessRequestIdentifier", source = "accessRequest.identifier")
    ExecutorResultReceivedEvent.ExecutorResultReceivedEventExecutorResponse toEventExecutorResponse(ExecutorResponse entity);

    default ObjectNode stringToProperties(String properties) {
        if (properties == null || properties.isEmpty()) {
            return null;
        }
        try {
            return new ObjectMapper().readValue(properties, ObjectNode.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting string to properties", e);
        }
    }
} 