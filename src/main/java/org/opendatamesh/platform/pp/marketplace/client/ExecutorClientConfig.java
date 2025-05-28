package org.opendatamesh.platform.pp.marketplace.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorClientConfig {

    @Bean
    public ExecutorClient executorClient() {
        return new ExecutorClientImpl();
    }
}
