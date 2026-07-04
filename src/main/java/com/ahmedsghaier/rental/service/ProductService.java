package com.ahmedsghaier.rental.service;

import com.ahmedsghaier.rental.domain.Product;
import com.ahmedsghaier.rental.domain.ProductAvailability;
import com.ahmedsghaier.rental.domain.exception.ValidationException;
import com.ahmedsghaier.rental.persistence.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Application service for managing catalogue {@link Product}s and reporting their
 * availability.
 */
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<ProductAvailability> findAllWithAvailability() {
        return productRepository.findAllWithAvailability();
    }

    /** @return only the products that are currently free to rent. */
    public List<ProductAvailability> findAvailable() {
        return productRepository.findAllWithAvailability().stream()
                .filter(ProductAvailability::isAvailable)
                .toList();
    }

    /**
     * Validates and persists a product (insert or update).
     *
     * @throws ValidationException if the label is missing, the price is negative, or no
     *                             category is assigned
     */
    public Product save(Product product) {
        validate(product);
        return productRepository.save(product);
    }

    public void delete(Product product) {
        productRepository.deleteById(product.getId());
    }

    private void validate(Product product) {
        if (product == null) {
            throw new ValidationException("Product must not be null.");
        }
        if (product.getLabel() == null || product.getLabel().isBlank()) {
            throw new ValidationException("Die Produktbezeichnung darf nicht leer sein.");
        }
        if (product.getDailyPrice() == null
                || product.getDailyPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Der Preis muss 0 oder größer sein.");
        }
        if (product.getCategory() == null) {
            throw new ValidationException("Bitte wählen Sie eine Kategorie aus.");
        }
    }
}
