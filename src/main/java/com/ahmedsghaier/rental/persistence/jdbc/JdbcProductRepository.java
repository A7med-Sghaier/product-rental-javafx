package com.ahmedsghaier.rental.persistence.jdbc;

import com.ahmedsghaier.rental.domain.Category;
import com.ahmedsghaier.rental.domain.Product;
import com.ahmedsghaier.rental.domain.ProductAvailability;
import com.ahmedsghaier.rental.domain.RentalStatus;
import com.ahmedsghaier.rental.domain.exception.DataAccessException;
import com.ahmedsghaier.rental.persistence.ConnectionFactory;
import com.ahmedsghaier.rental.persistence.ProductRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC/SQLite implementation of {@link ProductRepository} using prepared statements.
 */
public class JdbcProductRepository implements ProductRepository {

    private final ConnectionFactory connectionFactory;

    public JdbcProductRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Product> findAll() {
        String sql = """
                SELECT p.id, p.label, p.preis, c.id AS category_id, c.label AS category_label
                FROM products p
                INNER JOIN categories c ON p.categorie_id = c.id
                ORDER BY p.label""";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
            return products;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load products", e);
        }
    }

    @Override
    public List<ProductAvailability> findAllWithAvailability() {
        String sql = """
                SELECT p.id, p.label, p.preis, c.id AS category_id, c.label AS category_label,
                       r.status AS rent_status, r.date_from, r.date_to
                FROM products p
                INNER JOIN categories c ON p.categorie_id = c.id
                LEFT JOIN rents r ON p.id = r.p_id AND r.status != ?
                ORDER BY p.label""";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, RentalStatus.RETURNED.dbValue());
            try (ResultSet rs = statement.executeQuery()) {
                List<ProductAvailability> result = new ArrayList<>();
                while (rs.next()) {
                    Product product = mapProduct(rs);
                    RentalStatus status = RentalStatus.fromDbValue(rs.getString("rent_status"));
                    result.add(new ProductAvailability(
                            product,
                            status,
                            JdbcSupport.parseDate(rs.getString("date_from")),
                            JdbcSupport.parseDate(rs.getString("date_to"))));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load product availability", e);
        }
    }

    @Override
    public Product save(Product product) {
        return product.isNew() ? insert(product) : update(product);
    }

    private Product insert(Product product) {
        String sql = "INSERT INTO products (label, preis, categorie_id) VALUES (?, ?, ?)";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, product.getLabel());
            statement.setBigDecimal(2, product.getDailyPrice());
            statement.setInt(3, product.getCategory().getId());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    product.setId(keys.getInt(1));
                }
            }
            return product;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert product", e);
        }
    }

    private Product update(Product product) {
        String sql = "UPDATE products SET label = ?, preis = ?, categorie_id = ? WHERE id = ?";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getLabel());
            statement.setBigDecimal(2, product.getDailyPrice());
            statement.setInt(3, product.getCategory().getId());
            statement.setInt(4, product.getId());
            statement.executeUpdate();
            return product;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update product " + product.getId(), e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete product " + id, e);
        }
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        BigDecimal price = rs.getBigDecimal("preis");
        Category category = new Category(rs.getInt("category_id"), rs.getString("category_label"));
        return new Product(rs.getInt("id"), rs.getString("label"),
                price == null ? BigDecimal.ZERO : price, category);
    }
}
