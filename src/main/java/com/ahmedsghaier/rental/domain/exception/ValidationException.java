package com.ahmedsghaier.rental.domain.exception;

/**
 * Thrown when a domain object fails a business-rule or input validation check.
 *
 * <p>This is an unchecked exception so that callers are not forced to wrap every
 * service call, while the UI layer can still catch it to present a friendly message.</p>
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
