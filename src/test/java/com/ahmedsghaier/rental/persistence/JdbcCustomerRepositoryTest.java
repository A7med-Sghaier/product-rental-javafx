package com.ahmedsghaier.rental.persistence;

import com.ahmedsghaier.rental.domain.Customer;
import com.ahmedsghaier.rental.persistence.jdbc.JdbcCustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcCustomerRepositoryTest extends RepositoryTestBase {

    private JdbcCustomerRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JdbcCustomerRepository(connectionFactory);
    }

    private Customer sample(String first, String last) {
        Customer customer = new Customer();
        customer.setFirstName(first);
        customer.setLastName(last);
        customer.setCity("Berlin");
        return customer;
    }

    @Test
    void insertAssignsGeneratedIdAndPersists() {
        Customer saved = repository.save(sample("Ada", "Lovelace"));

        assertTrue(saved.getId() > 0);
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void findByIdReturnsPersistedCustomer() {
        Customer saved = repository.save(sample("Grace", "Hopper"));

        Optional<Customer> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Hopper", found.get().getLastName());
    }

    @Test
    void updateChangesExistingRow() {
        Customer saved = repository.save(sample("Alan", "Turing"));
        saved.setCity("Cambridge");

        repository.save(saved);

        assertEquals("Cambridge", repository.findById(saved.getId()).orElseThrow().getCity());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void deleteRemovesCustomer() {
        Customer saved = repository.save(sample("Edsger", "Dijkstra"));

        repository.deleteById(saved.getId());

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("names containing a single quote are stored literally (no SQL injection)")
    void storesQuotedInputLiterally() {
        Customer saved = repository.save(sample("O'Brien", "D'Angelo"));

        Customer reloaded = repository.findById(saved.getId()).orElseThrow();

        assertEquals("O'Brien", reloaded.getFirstName());
        assertEquals("D'Angelo", reloaded.getLastName());
        assertFalse(repository.findAll().isEmpty());
    }
}
