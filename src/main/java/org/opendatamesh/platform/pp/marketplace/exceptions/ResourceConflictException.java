package org.opendatamesh.platform.pp.marketplace.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceConflictException extends MarketplaceApiException {
    public ResourceConflictException(String message) {
        super(message);
    }

    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
