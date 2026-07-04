package com.ahmedsghaier.rental.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class RentalTest {

    @Test
    @DisplayName("getDays returns the number of days between the two dates")
    void computesDays() {
        Rental rental = new Rental();
        rental.setDateFrom(LocalDate.of(2026, 7, 1));
        rental.setDateTo(LocalDate.of(2026, 7, 8));

        assertEquals(7, rental.getDays());
    }

    @Test
    @DisplayName("getDays is zero when either date is missing")
    void zeroDaysWhenDatesMissing() {
        assertEquals(0, new Rental().getDays());
    }

    @Test
    @DisplayName("getDays is never negative for an inverted period")
    void neverNegativeDays() {
        Rental rental = new Rental();
        rental.setDateFrom(LocalDate.of(2026, 7, 8));
        rental.setDateTo(LocalDate.of(2026, 7, 1));

        assertEquals(0, rental.getDays());
    }

    @Test
    @DisplayName("getTotal multiplies the daily price by the number of days")
    void computesTotal() {
        Rental rental = new Rental();
        rental.setDailyPrice(new BigDecimal("12.50"));
        rental.setDateFrom(LocalDate.of(2026, 7, 1));
        rental.setDateTo(LocalDate.of(2026, 7, 5));

        assertEquals(0, new BigDecimal("50.00").compareTo(rental.getTotal()));
    }

    @Test
    @DisplayName("factory builds a RENTED rental carrying the product's price and label")
    void factoryBuildsRentedRental() {
        Product product = new Product(3, "Bohrmaschine", new BigDecimal("9.90"),
                new Category(1, "Technik"));

        Rental rental = Rental.of(42, product,
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 4));

        assertEquals(42, rental.getCustomerId());
        assertEquals(3, rental.getProductId());
        assertEquals("Bohrmaschine", rental.getProductLabel());
        assertSame(RentalStatus.RENTED, rental.getStatus());
        assertEquals(0, new BigDecimal("29.70").compareTo(rental.getTotal()));
    }
}
