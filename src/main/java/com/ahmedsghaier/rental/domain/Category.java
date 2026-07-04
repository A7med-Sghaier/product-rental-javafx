package com.ahmedsghaier.rental.domain;

import java.util.Objects;

/**
 * A product category (for example {@code Elektronik & Computer}).
 *
 * <p>A plain domain object with no UI or persistence dependencies. Identity is based on
 * the database {@code id}; a value of {@code 0} means the category has not been persisted
 * yet.</p>
 */
public class Category {

    private int id;
    private String label;

    public Category() {
    }

    public Category(String label) {
        this.label = label;
    }

    public Category(int id, String label) {
        this.id = id;
        this.label = label;
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

    /** @return {@code true} if this category has not been persisted yet. */
    public boolean isNew() {
        return id == 0;
    }

    /** Displayed directly in JavaFX {@code ChoiceBox}/{@code ComboBox} controls. */
    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Category category)) {
            return false;
        }
        return id == category.id && Objects.equals(label, category.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label);
    }
}
