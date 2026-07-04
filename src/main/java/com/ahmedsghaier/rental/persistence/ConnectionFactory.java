package com.ahmedsghaier.rental.persistence;

import com.ahmedsghaier.rental.domain.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Supplies JDBC {@link Connection}s for a single SQLite database URL.
 *
 * <p>Centralising connection creation keeps the JDBC URL in one place and lets tests point
 * the whole application at an in-memory database. Foreign-key enforcement is enabled on
 * every connection because SQLite disables it by default.</p>
 */
public class ConnectionFactory {

    /** Default on-disk database, matching the original application. */
    public static final String DEFAULT_URL = "jdbc:sqlite:Laiheus.db";

    private final String jdbcUrl;

    public ConnectionFactory(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /** @return a connection factory backed by the default on-disk database. */
    public static ConnectionFactory ofDefault() {
        return new ConnectionFactory(DEFAULT_URL);
    }

    /**
     * Opens a new connection with foreign-key constraints enabled.
     *
     * @return an open JDBC connection; the caller is responsible for closing it
     * @throws DataAccessException if the connection cannot be established
     */
    public Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl);
            try (var statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }
            return connection;
        } catch (SQLException e) {
            throw new DataAccessException("Could not open database connection: " + jdbcUrl, e);
        }
    }
}
