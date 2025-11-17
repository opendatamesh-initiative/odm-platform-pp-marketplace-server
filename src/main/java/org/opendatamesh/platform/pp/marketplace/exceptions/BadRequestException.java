package org.opendatamesh.platform.pp.marketplace.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends MarketplaceApiException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
