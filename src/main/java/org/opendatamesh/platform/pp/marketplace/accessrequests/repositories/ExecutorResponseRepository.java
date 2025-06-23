package org.opendatamesh.platform.pp.marketplace.accessrequests.repositories;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest_;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse_;
import org.opendatamesh.platform.pp.marketplace.utils.repositories.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.marketplace.utils.repositories.SpecsUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutorResponseRepository extends PagingAndSortingAndSpecificationExecutorRepository<ExecutorResponse, Long> {
    class Specs extends SpecsUtils {

        public static Specification<ExecutorResponse> hasAccessRequestIdentifier(String accessRequestIdentifier) {
            return (root, query, cb) -> cb.equal(root.get(ExecutorResponse_.accessRequestIdentifier), accessRequestIdentifier);
        }

        public static Specification<ExecutorResponse> hasAccessRequestUuid(String accessRequestUuid) {
            return (root, query, cb) -> cb.equal(root.get(ExecutorResponse_.accessRequest).get(AccessRequest_.uuid), accessRequestUuid);
        }
    }
} 