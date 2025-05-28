package org.opendatamesh.platform.pp.marketplace.exceptions;

import org.springframework.http.HttpStatus;

public abstract class MarketplaceException extends RuntimeException {
	
	private static final long serialVersionUID = 3876573329263306459L;	
	
	private final String errorCode;
	private final HttpStatus status;
	
	protected MarketplaceException(String message, String errorCode, HttpStatus status) {
		super(message);
		this.errorCode = errorCode;
		this.status = status;
	}
	
	protected MarketplaceException(String message, String errorCode, HttpStatus status, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
		this.status = status;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public HttpStatus getStatus() {
		return status;
	}
	
	public String getErrorName() {
		return getClass().getSimpleName();	
	}
	
}