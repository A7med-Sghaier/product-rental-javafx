package com.ahmedsghaier.rental.service;

import com.ahmedsghaier.rental.domain.Category;
import com.ahmedsghaier.rental.domain.exception.ValidationException;
import com.ahmedsghaier.rental.persistence.CategoryRepository;

import java.util.List;

/**
 * Application service for managing product {@link Category}s.
 */
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    /**
     * Validates and persists a category (insert or update).
     *
     * @throws ValidationException if the label is missing
     */
    public Category save(Category category) {
        if (category == null || category.getLabel() == null || category.getLabel().isBlank()) {
            throw new ValidationException("Die Bezeichnung darf nicht leer sein.");
        }
        category.setLabel(category.getLabel().trim());
        return categoryRepository.save(category);
    }

    public void delete(Category category) {
        categoryRepository.deleteById(category.getId());
    }
}
