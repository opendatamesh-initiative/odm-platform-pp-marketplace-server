package org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;

@Mapper(componentModel = "spring")
public interface AccessRequestMapper {

    @Mapping(target = "properties", expression = "java(propertiesToString(resource.getProperties()))")
    AccessRequest toEntity(AccessRequestRes resource);

    @Mapping(target = "properties", expression = "java(stringToProperties(entity.getProperties()))")
    AccessRequestRes toResource(AccessRequest entity);

    @Mapping(target = "properties", expression = "java(stringToProperties(entity.getProperties()))")
    void updateResourceFromEntity(AccessRequest entity, @MappingTarget AccessRequestRes resource);

    default String propertiesToString(ObjectNode properties) {
        if (properties == null) {
            return null;
        }
        try {
            return new ObjectMapper().writeValueAsString(properties);
        } catch (Exception e) {
            throw new RuntimeException("Error converting properties to string", e);
        }
    }

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
