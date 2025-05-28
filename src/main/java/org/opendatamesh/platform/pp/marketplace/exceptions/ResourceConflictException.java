package org.opendatamesh.platform.pp.marketplace.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceConflictException extends MarketplaceException {
    
    private static final long serialVersionUID = 1L;
    private static final String ERROR_CODE = "CONFLICT";
    
    public ResourceConflictException(String message) {
        super(message, ERROR_CODE, HttpStatus.CONFLICT);
    }
    
    public ResourceConflictException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.CONFLICT, cause);
    }
}