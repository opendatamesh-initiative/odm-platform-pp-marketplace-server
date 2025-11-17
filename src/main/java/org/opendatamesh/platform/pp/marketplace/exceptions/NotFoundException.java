package org.opendatamesh.platform.pp.marketplace.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends MarketplaceApiException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
