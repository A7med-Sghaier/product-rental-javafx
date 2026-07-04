package com.ahmedsghaier.rental.persistence;

import com.ahmedsghaier.rental.domain.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Persistence operations for {@link Customer} aggregates.
 */
public interface CustomerRepository {

    /** @return all customers. */
    List<Customer> findAll();

    /** @return only customers that currently have at least one active (rented) rental. */
    List<Customer> findAllWithActiveRentals();

    /** @return the customer with the given id, if present. */
    Optional<Customer> findById(int id);

    /**
     * Inserts a new customer (id {@code 0}) or updates an existing one.
     *
     * @return the persisted customer, with its generated id populated on insert
     */
    Customer save(Customer customer);

    /** Deletes the customer with the given id (cascades to their rentals). */
    void deleteById(int id);
}
