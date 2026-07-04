package com.ahmedsghaier.rental.persistence.jdbc;

import com.ahmedsghaier.rental.domain.Rental;
import com.ahmedsghaier.rental.domain.RentalStatus;
import com.ahmedsghaier.rental.domain.exception.DataAccessException;
import com.ahmedsghaier.rental.persistence.ConnectionFactory;
import com.ahmedsghaier.rental.persistence.RentalRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC/SQLite implementation of {@link RentalRepository} using prepared statements.
 *
 * <p>{@link #saveAll(List)} inserts all rentals inside a single transaction so a partial
 * batch can never be committed.</p>
 */
public class JdbcRentalRepository implements RentalRepository {

    private final ConnectionFactory connectionFactory;

    public JdbcRentalRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Rental> findActive() {
        String sql = """
                SELECT r.id, r.c_id, r.p_id, r.status, r.date_from, r.date_to,
                       p.label AS product_label, p.preis,
                       c.firstname, c.lastname
                FROM rents r
                LEFT JOIN products p ON r.p_id = p.id
                LEFT JOIN clients c ON r.c_id = c.id
                WHERE r.status = ?
                ORDER BY r.date_from DESC""";
        return queryRentals(sql, RentalStatus.RENTED, null);
    }

    @Override
    public List<Rental> findActiveByCustomerId(int customerId) {
        String sql = """
                SELECT r.id, r.c_id, r.p_id, r.status, r.date_from, r.date_to,
                       p.label AS product_label, p.preis,
                       c.firstname, c.lastname
                FROM rents r
                LEFT JOIN products p ON r.p_id = p.id
                LEFT JOIN clients c ON r.c_id = c.id
                WHERE r.status = ? AND r.c_id = ?
                ORDER BY r.date_from DESC""";
        return queryRentals(sql, RentalStatus.RENTED, customerId);
    }

    private List<Rental> queryRentals(String sql, RentalStatus status, Integer customerId) {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.dbValue());
            if (customerId != null) {
                statement.setInt(2, customerId);
            }
            try (ResultSet rs = statement.executeQuery()) {
                List<Rental> rentals = new ArrayList<>();
                while (rs.next()) {
                    rentals.add(map(rs));
                }
                return rentals;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load rentals", e);
        }
    }

    @Override
    public void saveAll(List<Rental> rentals) {
        String sql = "INSERT INTO rents (c_id, p_id, status, date_from, date_to) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connectionFactory.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (Rental rental : rentals) {
                    statement.setInt(1, rental.getCustomerId());
                    statement.setInt(2, rental.getProductId());
                    statement.setString(3, rental.getStatus().dbValue());
                    statement.setString(4, JdbcSupport.formatDate(rental.getDateFrom()));
                    statement.setString(5, JdbcSupport.formatDate(rental.getDateTo()));
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save rentals", e);
        }
    }

    @Override
    public void updateStatus(int rentalId, RentalStatus status) {
        String sql = "UPDATE rents SET status = ? WHERE id = ?";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.dbValue());
            statement.setInt(2, rentalId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update rental " + rentalId, e);
        }
    }

    private Rental map(ResultSet rs) throws SQLException {
        Rental rental = new Rental();
        rental.setId(rs.getInt("id"));
        rental.setCustomerId(rs.getInt("c_id"));
        rental.setProductId(rs.getInt("p_id"));
        rental.setStatus(RentalStatus.fromDbValue(rs.getString("status")));
        rental.setDateFrom(JdbcSupport.parseDate(rs.getString("date_from")));
        rental.setDateTo(JdbcSupport.parseDate(rs.getString("date_to")));
        rental.setProductLabel(rs.getString("product_label"));
        BigDecimal price = rs.getBigDecimal("preis");
        rental.setDailyPrice(price == null ? BigDecimal.ZERO : price);

        String firstName = rs.getString("firstname");
        String lastName = rs.getString("lastname");
        if (firstName != null || lastName != null) {
            rental.setCustomerName(((firstName == null ? "" : firstName) + " "
                    + (lastName == null ? "" : lastName)).trim());
        }
        return rental;
    }
}
