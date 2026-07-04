package com.ahmedsghaier.rental.domain;

import java.util.Objects;

/**
 * A customer who can rent products.
 *
 * <p>Plain domain object (no JavaFX properties, no JDBC). The UI layer binds to the
 * standard getters via JavaFX {@code PropertyValueFactory}; the persistence layer maps
 * columns through the same accessors.</p>
 */
public class Customer {

    private int id;
    private String firstName;
    private String lastName;
    private String address;
    private String postalCode;
    private String city;
    private String phone;

    public Customer() {
    }

    public Customer(int id, String firstName, String lastName, String address,
                    String postalCode, String city, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** @return the customer's full name, convenient for table columns and labels. */
    public String getFullName() {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
    }

    /** @return {@code true} if this customer has not been persisted yet. */
    public boolean isNew() {
        return id == 0;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Customer customer)) {
            return false;
        }
        return id == customer.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return getFullName().trim();
    }
}
