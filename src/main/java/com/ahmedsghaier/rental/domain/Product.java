package com.ahmedsghaier.rental.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A catalogue product that can be rented out, priced per day.
 *
 * <p>Monetary values use {@link BigDecimal} rather than {@code float} to avoid rounding
 * errors when computing rental totals.</p>
 */
public class Product {

    private int id;
    private String label;
    private BigDecimal dailyPrice = BigDecimal.ZERO;
    private Category category;

    public Product() {
    }

    public Product(int id, String label, BigDecimal dailyPrice, Category category) {
        this.id = id;
        this.label = label;
        this.dailyPrice = dailyPrice == null ? BigDecimal.ZERO : dailyPrice;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(BigDecimal dailyPrice) {
        this.dailyPrice = dailyPrice == null ? BigDecimal.ZERO : dailyPrice;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    /** Convenience accessor for table columns bound to the category label. */
    public String getCategoryLabel() {
        return category == null ? "" : category.getLabel();
    }

    /** @return {@code true} if this product has not been persisted yet. */
    public boolean isNew() {
        return id == 0;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Product product)) {
            return false;
        }
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return label;
    }
}
