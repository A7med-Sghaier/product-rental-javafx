package com.ahmedsghaier.rental.service;

import com.ahmedsghaier.rental.domain.Category;
import com.ahmedsghaier.rental.domain.Product;
import com.ahmedsghaier.rental.domain.Rental;
import com.ahmedsghaier.rental.domain.RentalStatus;
import com.ahmedsghaier.rental.domain.exception.ValidationException;
import com.ahmedsghaier.rental.persistence.RentalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository repository;

    @InjectMocks
    private RentalService service;

    private Rental line(String price, int days) {
        Product product = new Product(1, "Beamer", new BigDecimal(price), new Category(1, "Technik"));
        LocalDate from = LocalDate.of(2026, 7, 1);
        return Rental.of(1, product, from, from.plusDays(days));
    }

    @Test
    void computeTotalSumsAllLines() {
        BigDecimal total = service.computeTotal(List.of(line("10.00", 2), line("5.00", 3)));
        assertEquals(0, new BigDecimal("35.00").compareTo(total));
    }

    @Test
    void checkoutRejectsEmptyBasket() {
        assertThrows(ValidationException.class, () -> service.checkout(List.of()));
        verify(repository, never()).saveAll(org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void checkoutRejectsInvertedPeriod() {
        Rental bad = line("10.00", 2);
        bad.setDateTo(bad.getDateFrom().minusDays(1));

        assertThrows(ValidationException.class, () -> service.checkout(List.of(bad)));
        verify(repository, never()).saveAll(org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void checkoutPersistsRentedLines() {
        Rental line = line("10.00", 2);

        service.checkout(List.of(line));

        ArgumentCaptor<List<Rental>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());
        assertEquals(RentalStatus.RENTED, captor.getValue().get(0).getStatus());
    }

    @Test
    void returnRentalUpdatesStatusToReturned() {
        Rental rental = line("10.00", 2);
        rental.setId(99);

        service.returnRental(rental);

        verify(repository).updateStatus(99, RentalStatus.RETURNED);
    }

    @Test
    void returnRentalRejectsUnsavedRental() {
        assertThrows(ValidationException.class, () -> service.returnRental(line("10.00", 2)));
    }
}
