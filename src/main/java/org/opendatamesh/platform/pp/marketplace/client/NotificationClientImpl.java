package org.opendatamesh.platform.pp.marketplace.client;

import org.opendatamesh.platform.pp.marketplace.exceptions.client.ClientException;
import org.opendatamesh.platform.pp.marketplace.utils.client.RestUtils;
import org.opendatamesh.platform.pp.marketplace.utils.client.RestUtilsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class NotificationClientImpl implements NotificationClient {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String NOTIFICATION_ENDPOINT = "api/v1/notifications";
    private final RestUtils restUtils;
    private final String notificationServiceBaseUrl;

    public NotificationClientImpl(String notificationServiceBaseUrl) {
        this.notificationServiceBaseUrl = notificationServiceBaseUrl;
        this.restUtils = RestUtilsFactory.getRestUtils(new RestTemplate());
    }

    @Override
    public void notifyEvent(Object event) {
        try {
            restUtils.genericPost(
                    String.format("%s/%s", notificationServiceBaseUrl, NOTIFICATION_ENDPOINT),
                    null,
                    event,
                    Object.class
            );
        } catch (ClientException e) {
            log.warn(e.getMessage(), e);
        }
    }
} 