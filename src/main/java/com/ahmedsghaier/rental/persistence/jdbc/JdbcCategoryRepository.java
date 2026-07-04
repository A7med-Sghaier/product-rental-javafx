package com.ahmedsghaier.rental.persistence.jdbc;

import com.ahmedsghaier.rental.domain.Category;
import com.ahmedsghaier.rental.domain.exception.DataAccessException;
import com.ahmedsghaier.rental.persistence.CategoryRepository;
import com.ahmedsghaier.rental.persistence.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC/SQLite implementation of {@link CategoryRepository} using prepared statements.
 */
public class JdbcCategoryRepository implements CategoryRepository {

    private final ConnectionFactory connectionFactory;

    public JdbcCategoryRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories ORDER BY label";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            List<Category> categories = new ArrayList<>();
            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("label")));
            }
            return categories;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load categories", e);
        }
    }

    @Override
    public Category save(Category category) {
        return category.isNew() ? insert(category) : update(category);
    }

    private Category insert(Category category) {
        String sql = "INSERT INTO categories (label) VALUES (?)";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, category.getLabel());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    category.setId(keys.getInt(1));
                }
            }
            return category;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert category", e);
        }
    }

    private Category update(Category category) {
        String sql = "UPDATE categories SET label = ? WHERE id = ?";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, category.getLabel());
            statement.setInt(2, category.getId());
            statement.executeUpdate();
            return category;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update category " + category.getId(), e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete category " + id, e);
        }
    }
}
