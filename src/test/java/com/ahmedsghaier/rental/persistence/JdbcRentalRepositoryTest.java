package com.ahmedsghaier.rental.persistence;

import com.ahmedsghaier.rental.domain.Category;
import com.ahmedsghaier.rental.domain.Customer;
import com.ahmedsghaier.rental.domain.Product;
import com.ahmedsghaier.rental.domain.ProductAvailability;
import com.ahmedsghaier.rental.domain.Rental;
import com.ahmedsghaier.rental.domain.RentalStatus;
import com.ahmedsghaier.rental.persistence.jdbc.JdbcCategoryRepository;
import com.ahmedsghaier.rental.persistence.jdbc.JdbcCustomerRepository;
import com.ahmedsghaier.rental.persistence.jdbc.JdbcProductRepository;
import com.ahmedsghaier.rental.persistence.jdbc.JdbcRentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcRentalRepositoryTest extends RepositoryTestBase {

    private JdbcRentalRepository rentals;
    private JdbcProductRepository products;
    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        rentals = new JdbcRentalRepository(connectionFactory);
        products = new JdbcProductRepository(connectionFactory);

        Customer c = new Customer();
        c.setFirstName("Ada");
        c.setLastName("Lovelace");
        customer = new JdbcCustomerRepository(connectionFactory).save(c);

        Category category = new JdbcCategoryRepository(connectionFactory).findAll().get(0);
        product = products.save(new Product(0, "Beamer", new BigDecimal("12.00"), category));
    }

    private Rental activeRental() {
        return Rental.of(customer.getId(), product,
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5));
    }

    @Test
    void saveAllPersistsActiveRentalsWithJoinedFields() {
        rentals.saveAll(List.of(activeRental()));

        List<Rental> active = rentals.findActive();
        assertEquals(1, active.size());
        assertEquals("Beamer", active.get(0).getProductLabel());
        assertEquals("Ada Lovelace", active.get(0).getCustomerName());
        assertEquals(0, new BigDecimal("12.00").compareTo(active.get(0).getDailyPrice()));
    }

    @Test
    void rentingAProductMakesItUnavailable() {
        rentals.saveAll(List.of(activeRental()));

        ProductAvailability availability = products.findAllWithAvailability().get(0);

        assertFalse(availability.isAvailable());
        assertEquals(RentalStatus.RENTED, availability.status());
    }

    @Test
    void findActiveByCustomerIdIsScopedToThatCustomer() {
        rentals.saveAll(List.of(activeRental()));

        assertEquals(1, rentals.findActiveByCustomerId(customer.getId()).size());
        assertTrue(rentals.findActiveByCustomerId(customer.getId() + 999).isEmpty());
    }

    @Test
    void returningARentalRemovesItFromActiveAndFreesTheProduct() {
        rentals.saveAll(List.of(activeRental()));
        int rentalId = rentals.findActive().get(0).getId();

        rentals.updateStatus(rentalId, RentalStatus.RETURNED);

        assertTrue(rentals.findActive().isEmpty());
        assertTrue(products.findAllWithAvailability().get(0).isAvailable());
    }
}
