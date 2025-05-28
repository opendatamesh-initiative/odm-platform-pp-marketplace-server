package org.opendatamesh.platform.pp.marketplace.accessrequests.services.submitter;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.configuration.executor.MarketplaceExecutorConfig;

import java.util.List;

interface AccessRequestSubmitterExecutorOutputPort {
    List<MarketplaceExecutorConfig.MarketplaceExecutor> getExecutorsConfig();

    void submitRequest(String address, AccessRequest accessRequest);
}
