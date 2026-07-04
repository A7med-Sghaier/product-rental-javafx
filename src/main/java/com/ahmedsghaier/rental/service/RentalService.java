package com.ahmedsghaier.rental.service;

import com.ahmedsghaier.rental.domain.Rental;
import com.ahmedsghaier.rental.domain.RentalStatus;
import com.ahmedsghaier.rental.domain.exception.ValidationException;
import com.ahmedsghaier.rental.persistence.RentalRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Application service that drives the rental and return workflows and computes totals.
 */
public class RentalService {

    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public List<Rental> findActive() {
        return rentalRepository.findActive();
    }

    public List<Rental> findActiveByCustomerId(int customerId) {
        return rentalRepository.findActiveByCustomerId(customerId);
    }

    /**
     * Sums the total price of a set of rental lines.
     *
     * @param rentals the lines to total (may be empty)
     * @return the combined total, never {@code null}
     */
    public BigDecimal computeTotal(List<Rental> rentals) {
        return rentals.stream()
                .map(Rental::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Validates a basket of rental lines and persists them as active rentals.
     *
     * @param rentals the lines to check out
     * @throws ValidationException if the basket is empty or any line has an invalid period
     */
    public void checkout(List<Rental> rentals) {
        if (rentals == null || rentals.isEmpty()) {
            throw new ValidationException("Bitte fügen Sie mindestens ein Produkt hinzu.");
        }
        for (Rental rental : rentals) {
            validatePeriod(rental);
            rental.setStatus(RentalStatus.RENTED);
        }
        rentalRepository.saveAll(rentals);
    }

    /**
     * Marks a rental as returned.
     *
     * @param rental the rental to close
     */
    public void returnRental(Rental rental) {
        if (rental == null || rental.getId() == 0) {
            throw new ValidationException("Bitte wählen Sie ein Produkt aus.");
        }
        rentalRepository.updateStatus(rental.getId(), RentalStatus.RETURNED);
    }

    private void validatePeriod(Rental rental) {
        if (rental.getDateFrom() == null || rental.getDateTo() == null) {
            throw new ValidationException("Bitte geben Sie Leih- und Rückgabedatum an.");
        }
        if (!rental.getDateTo().isAfter(rental.getDateFrom())) {
            throw new ValidationException(
                    "Das Rückgabedatum muss nach dem Leihdatum liegen.");
        }
    }
}
