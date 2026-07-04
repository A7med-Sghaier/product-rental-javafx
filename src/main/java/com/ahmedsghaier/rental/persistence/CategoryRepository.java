package com.ahmedsghaier.rental.persistence;

import com.ahmedsghaier.rental.domain.Category;

import java.util.List;

/**
 * Persistence operations for {@link Category} entities.
 */
public interface CategoryRepository {

    /** @return all categories ordered by label. */
    List<Category> findAll();

    /**
     * Inserts a new category (id {@code 0}) or updates an existing one.
     *
     * @return the persisted category, with its generated id populated on insert
     */
    Category save(Category category);

    /** Deletes the category with the given id (cascades to its products). */
    void deleteById(int id);
}
