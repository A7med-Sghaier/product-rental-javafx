package com.ahmedsghaier.rental.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Read model that pairs a catalogue {@link Product} with its current availability, used by
 * the products overview screen.
 *
 * <p>When the product is currently rented out, {@link #dateFrom()} and {@link #dateTo()}
 * describe the active rental period; otherwise they are {@code null} and the status is
 * {@link RentalStatus#AVAILABLE}.</p>
 *
 * <p>Getter-style aliases ({@code getLabel()}, {@code getStatusLabel()}, …) are provided so
 * the object can be bound directly by JavaFX {@code PropertyValueFactory}.</p>
 */
public record ProductAvailability(Product product, RentalStatus status,
                                  LocalDate dateFrom, LocalDate dateTo) {

    public int getProductId() {
        return product.getId();
    }

    public String getLabel() {
        return product.getLabel();
    }

    public String getCategoryLabel() {
        return product.getCategoryLabel();
    }

    public BigDecimal getDailyPrice() {
        return product.getDailyPrice();
    }

    public String getStatusLabel() {
        return status.dbValue();
    }

    public boolean isAvailable() {
        return status == RentalStatus.AVAILABLE;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }
}
