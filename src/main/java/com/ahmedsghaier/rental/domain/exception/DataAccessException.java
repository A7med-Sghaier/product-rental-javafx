package com.ahmedsghaier.rental.domain.exception;

/**
 * Wraps low-level persistence failures (such as {@link java.sql.SQLException}) in an
 * unchecked, layer-agnostic exception.
 *
 * <p>This keeps JDBC details from leaking into the service and UI layers: callers depend
 * on this domain exception instead of {@code java.sql}.</p>
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
