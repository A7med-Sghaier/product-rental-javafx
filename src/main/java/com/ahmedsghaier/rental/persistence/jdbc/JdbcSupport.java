package com.ahmedsghaier.rental.persistence.jdbc;

import java.time.LocalDate;

/**
 * Small helpers shared by the JDBC repositories.
 *
 * <p>Dates are stored as ISO-8601 strings (for example {@code 2026-07-04}) to match the
 * format produced by the original application.</p>
 */
final class JdbcSupport {

    private JdbcSupport() {
    }

    /**
     * Parses an ISO date string into a {@link LocalDate}.
     *
     * @param value the stored string (may be {@code null} or blank)
     * @return the parsed date, or {@code null} if the input is empty
     */
    static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
    }

    /** @return the ISO string for a date, or {@code null} if the date is {@code null}. */
    static String formatDate(LocalDate value) {
        return value == null ? null : value.toString();
    }
}
