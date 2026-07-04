package com.ahmedsghaier.rental.persistence;

import com.ahmedsghaier.rental.domain.Rental;
import com.ahmedsghaier.rental.domain.RentalStatus;

import java.util.List;

/**
 * Persistence operations for {@link Rental} records.
 */
public interface RentalRepository {

    /** @return all rentals whose status is {@link RentalStatus#RENTED}. */
    List<Rental> findActive();

    /** @return the active (rented) rentals belonging to a single customer. */
    List<Rental> findActiveByCustomerId(int customerId);

    /** Persists a batch of new rentals in a single transaction. */
    void saveAll(List<Rental> rentals);

    /** Updates the status of the rental with the given id. */
    void updateStatus(int rentalId, RentalStatus status);
}
