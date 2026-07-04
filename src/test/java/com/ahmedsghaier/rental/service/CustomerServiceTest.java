package com.ahmedsghaier.rental.service;

import com.ahmedsghaier.rental.domain.Customer;
import com.ahmedsghaier.rental.domain.exception.ValidationException;
import com.ahmedsghaier.rental.persistence.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    private Customer valid() {
        Customer customer = new Customer();
        customer.setFirstName("Ada");
        customer.setLastName("Lovelace");
        return customer;
    }

    @Test
    void savesValidCustomer() {
        Customer customer = valid();
        when(repository.save(customer)).thenReturn(customer);

        assertEquals(customer, service.save(customer));
        verify(repository).save(customer);
    }

    @Test
    void rejectsBlankFirstName() {
        Customer customer = valid();
        customer.setFirstName("  ");

        assertThrows(ValidationException.class, () -> service.save(customer));
        verify(repository, never()).save(customer);
    }

    @Test
    void rejectsMissingLastName() {
        Customer customer = valid();
        customer.setLastName(null);

        assertThrows(ValidationException.class, () -> service.save(customer));
        verify(repository, never()).save(customer);
    }

    @Test
    void deleteDelegatesToRepository() {
        Customer customer = valid();
        customer.setId(7);

        service.delete(customer);

        verify(repository).deleteById(7);
    }
}
