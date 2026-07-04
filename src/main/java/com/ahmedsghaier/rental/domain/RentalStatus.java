package com.ahmedsghaier.rental.domain;

import java.util.Arrays;

/**
 * Lifecycle state of a {@link Rental}.
 *
 * <p>The {@link #dbValue()} is the string that is persisted in the {@code rents.status}
 * column. It intentionally matches the values used by the original application so that
 * existing databases remain readable.</p>
 */
public enum RentalStatus {

    /** The product is in the catalogue and free to be rented. */
    AVAILABLE("verfügbar"),

    /** The product is currently rented out to a customer. */
    RENTED("ausgeliehen"),

    /** The rental has been closed and the product returned. */
    RETURNED("returned");

    private final String dbValue;

    RentalStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    /** @return the value stored in the database for this status. */
    public String dbValue() {
        return dbValue;
    }

    /**
     * Resolves a status from its persisted database value.
     *
     * @param value the stored value (may be {@code null})
     * @return the matching status, defaulting to {@link #AVAILABLE} for {@code null}/unknown values
     */
    public static RentalStatus fromDbValue(String value) {
        if (value == null) {
            return AVAILABLE;
        }
        return Arrays.stream(values())
                .filter(status -> status.dbValue.equalsIgnoreCase(value))
                .findFirst()
                .orElse(AVAILABLE);
    }
}
