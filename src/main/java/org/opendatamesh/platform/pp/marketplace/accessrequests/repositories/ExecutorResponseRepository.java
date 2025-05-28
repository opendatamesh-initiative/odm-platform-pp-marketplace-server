package org.opendatamesh.platform.pp.marketplace.accessrequests.repositories;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;
import org.opendatamesh.platform.pp.marketplace.utils.repositories.PagingAndSortingAndSpecificationExecutorRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutorResponseRepository extends PagingAndSortingAndSpecificationExecutorRepository<ExecutorResponse, Long> {
} 