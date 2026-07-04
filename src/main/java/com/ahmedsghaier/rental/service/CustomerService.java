package com.ahmedsghaier.rental.service;

import com.ahmedsghaier.rental.domain.Customer;
import com.ahmedsghaier.rental.domain.exception.ValidationException;
import com.ahmedsghaier.rental.persistence.CustomerRepository;

import java.util.List;
import java.util.Optional;

/**
 * Application service for managing {@link Customer}s.
 *
 * <p>Enforces input validation before delegating persistence to a
 * {@link CustomerRepository}. The UI never talks to repositories directly.</p>
 */
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public List<Customer> findAllWithActiveRentals() {
        return customerRepository.findAllWithActiveRentals();
    }

    public Optional<Customer> findById(int id) {
        return customerRepository.findById(id);
    }

    /**
     * Validates and persists a customer (insert or update).
     *
     * @throws ValidationException if first or last name is missing
     */
    public Customer save(Customer customer) {
        validate(customer);
        return customerRepository.save(customer);
    }

    public void delete(Customer customer) {
        customerRepository.deleteById(customer.getId());
    }

    private void validate(Customer customer) {
        if (customer == null) {
            throw new ValidationException("Customer must not be null.");
        }
        if (isBlank(customer.getFirstName())) {
            throw new ValidationException("Der Vorname darf nicht leer sein.");
        }
        if (isBlank(customer.getLastName())) {
            throw new ValidationException("Der Nachname darf nicht leer sein.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
