package org.opendatamesh.platform.pp.marketplace.configuration.executor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "odm.utility-plane")
public class MarketplaceExecutorConfig {

    private List<MarketplaceExecutor> marketplaceExecutors;

    public List<MarketplaceExecutor> getMarketplaceExecutors() {
        return marketplaceExecutors;
    }

    public void setMarketplaceExecutors(List<MarketplaceExecutor> marketplaceExecutors) {
        this.marketplaceExecutors = marketplaceExecutors;
    }

    public static class MarketplaceExecutor {
        private String name;
        private String address;
        private boolean active;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}
