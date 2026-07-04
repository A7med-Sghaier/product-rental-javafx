package com.ahmedsghaier.rental.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class RentalStatusTest {

    @Test
    void dbValuesMatchLegacyStrings() {
        assertEquals("verfügbar", RentalStatus.AVAILABLE.dbValue());
        assertEquals("ausgeliehen", RentalStatus.RENTED.dbValue());
        assertEquals("returned", RentalStatus.RETURNED.dbValue());
    }

    @Test
    void resolvesEachStatusFromItsDbValue() {
        assertSame(RentalStatus.AVAILABLE, RentalStatus.fromDbValue("verfügbar"));
        assertSame(RentalStatus.RENTED, RentalStatus.fromDbValue("ausgeliehen"));
        assertSame(RentalStatus.RETURNED, RentalStatus.fromDbValue("returned"));
    }

    @Test
    void defaultsToAvailableForNullOrUnknownValues() {
        assertSame(RentalStatus.AVAILABLE, RentalStatus.fromDbValue(null));
        assertSame(RentalStatus.AVAILABLE, RentalStatus.fromDbValue("something-else"));
    }
}
