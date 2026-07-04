package com.ahmedsghaier.rental.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {

    @Test
    void isNewReflectsWhetherIdIsAssigned() {
        assertTrue(new Product().isNew());
        assertFalse(new Product(5, "Kamera", BigDecimal.TEN, new Category(1, "Technik")).isNew());
    }

    @Test
    void categoryLabelIsNullSafe() {
        assertEquals("", new Product().getCategoryLabel());
        assertEquals("Technik",
                new Product(1, "Kamera", BigDecimal.TEN, new Category(1, "Technik"))
                        .getCategoryLabel());
    }

    @Test
    void nullDailyPriceIsNormalisedToZero() {
        Product product = new Product();
        product.setDailyPrice(null);
        assertEquals(0, BigDecimal.ZERO.compareTo(product.getDailyPrice()));
    }
}
