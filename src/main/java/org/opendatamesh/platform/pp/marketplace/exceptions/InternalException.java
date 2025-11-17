package org.opendatamesh.platform.pp.marketplace.exceptions;

import org.springframework.http.HttpStatus;

public class InternalException extends MarketplaceApiException {
    public InternalException(String message) {
        super(message);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
