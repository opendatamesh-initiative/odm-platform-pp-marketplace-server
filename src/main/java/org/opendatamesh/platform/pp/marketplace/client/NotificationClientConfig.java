package org.opendatamesh.platform.pp.marketplace.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationClientConfig {
    private static final Logger logger = LoggerFactory.getLogger(NotificationClientConfig.class);

    @Value("${odm.product-plane.notification-service.address}")
    private String notificationServiceBaseUrl;

    @Value("${odm.product-plane.notification-service.active}")
    private boolean notificationServiceActive;

    @Bean
    public NotificationClient notificationClient() {
        if (notificationServiceActive) {
            return new NotificationClientImpl(notificationServiceBaseUrl);
        } else {
            logger.warn("Notification service is not active. Events will not be sent.");
            return new NotificationClient() {
                @Override
                public void notifyEvent(Object event) {
                    logger.warn("Notification service is not active. Event not sent: {}", event);
                }
            };
        }
    }
}
