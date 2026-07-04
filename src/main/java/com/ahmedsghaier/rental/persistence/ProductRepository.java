package com.ahmedsghaier.rental.persistence;

import com.ahmedsghaier.rental.domain.Product;
import com.ahmedsghaier.rental.domain.ProductAvailability;

import java.util.List;

/**
 * Persistence operations for {@link Product} entities.
 */
public interface ProductRepository {

    /** @return the full product catalogue. */
    List<Product> findAll();

    /**
     * @return every product paired with its current availability (whether it is free or
     *         currently rented out, and if so for which period).
     */
    List<ProductAvailability> findAllWithAvailability();

    /**
     * Inserts a new product (id {@code 0}) or updates an existing one.
     *
     * @return the persisted product, with its generated id populated on insert
     */
    Product save(Product product);

    /** Deletes the product with the given id. */
    void deleteById(int id);
}
