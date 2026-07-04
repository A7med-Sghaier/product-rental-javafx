package com.ahmedsghaier.rental.persistence.jdbc;

import com.ahmedsghaier.rental.domain.Customer;
import com.ahmedsghaier.rental.domain.exception.DataAccessException;
import com.ahmedsghaier.rental.persistence.ConnectionFactory;
import com.ahmedsghaier.rental.persistence.CustomerRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC/SQLite implementation of {@link CustomerRepository}.
 *
 * <p>Every query uses {@link PreparedStatement} with bound parameters, eliminating the SQL
 * injection risk present in the original string-concatenated implementation.</p>
 */
public class JdbcCustomerRepository implements CustomerRepository {

    private final ConnectionFactory connectionFactory;

    public JdbcCustomerRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT * FROM clients ORDER BY lastname, firstname";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return mapAll(rs);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load customers", e);
        }
    }

    @Override
    public List<Customer> findAllWithActiveRentals() {
        String sql = """
                SELECT DISTINCT c.* FROM clients c
                JOIN rents r ON c.id = r.c_id
                WHERE r.status = ?
                ORDER BY c.lastname, c.firstname""";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, com.ahmedsghaier.rental.domain.RentalStatus.RENTED.dbValue());
            try (ResultSet rs = statement.executeQuery()) {
                return mapAll(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load customers with active rentals", e);
        }
    }

    @Override
    public Optional<Customer> findById(int id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load customer " + id, e);
        }
    }

    @Override
    public Customer save(Customer customer) {
        return customer.isNew() ? insert(customer) : update(customer);
    }

    private Customer insert(Customer customer) {
        String sql = "INSERT INTO clients (firstname, lastname, address, plz, city, tel) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindFields(statement, customer);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    customer.setId(keys.getInt(1));
                }
            }
            return customer;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert customer", e);
        }
    }

    private Customer update(Customer customer) {
        String sql = "UPDATE clients SET firstname = ?, lastname = ?, address = ?, "
                + "plz = ?, city = ?, tel = ? WHERE id = ?";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindFields(statement, customer);
            statement.setInt(7, customer.getId());
            statement.executeUpdate();
            return customer;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update customer " + customer.getId(), e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete customer " + id, e);
        }
    }

    private void bindFields(PreparedStatement statement, Customer customer) throws SQLException {
        statement.setString(1, customer.getFirstName());
        statement.setString(2, customer.getLastName());
        statement.setString(3, customer.getAddress());
        statement.setString(4, customer.getPostalCode());
        statement.setString(5, customer.getCity());
        statement.setString(6, customer.getPhone());
    }

    private List<Customer> mapAll(ResultSet rs) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        while (rs.next()) {
            customers.add(map(rs));
        }
        return customers;
    }

    private Customer map(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("id"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getString("address"),
                rs.getString("plz"),
                rs.getString("city"),
                rs.getString("tel"));
    }
}
