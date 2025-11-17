package org.opendatamesh.platform.pp.marketplace.exceptions;

import org.springframework.http.HttpStatus;

public abstract class MarketplaceApiException extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = 3876573329263306459L;

    public MarketplaceApiException() {
        super();
    }

    public MarketplaceApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MarketplaceApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarketplaceApiException(String message) {
        super(message);
    }

    public MarketplaceApiException(Throwable cause) {
        super(cause);
    }

    /**
     * @return the errorName
     */
    public String getErrorName() {
        return getClass().getSimpleName();
    }

    /**
     * @return the status
     */
    public abstract HttpStatus getStatus();


}