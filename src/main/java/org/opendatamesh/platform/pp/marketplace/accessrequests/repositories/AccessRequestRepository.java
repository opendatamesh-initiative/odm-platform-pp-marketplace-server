package org.opendatamesh.platform.pp.marketplace.accessrequests.repositories;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest_;
import org.opendatamesh.platform.pp.marketplace.utils.repositories.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.marketplace.utils.repositories.SpecsUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessRequestRepository extends PagingAndSortingAndSpecificationExecutorRepository<AccessRequest, String> {
    class Specs extends SpecsUtils {

        public static Specification<AccessRequest> hasIdentifier(String identifier) {
            return (root, query, cb) -> cb.equal(root.get(AccessRequest_.identifier), identifier);
        }
    }
} 