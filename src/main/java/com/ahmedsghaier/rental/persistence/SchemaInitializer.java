package com.ahmedsghaier.rental.persistence;

import com.ahmedsghaier.rental.domain.exception.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates the database schema and seeds default categories if they are absent.
 *
 * <p>All statements use {@code IF NOT EXISTS} / {@code INSERT OR IGNORE} so that
 * {@link #initialize()} is idempotent and safe to run on every application start.</p>
 */
public class SchemaInitializer {

    private final ConnectionFactory connectionFactory;

    public SchemaInitializer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Ensures all tables exist and the default categories are present.
     *
     * @throws DataAccessException if the schema cannot be created
     */
    public void initialize() {
        try (Connection connection = connectionFactory.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS clients (
                        id integer PRIMARY KEY,
                        firstname string,
                        lastname string,
                        address string,
                        plz string,
                        city string,
                        tel string
                    )""");

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id integer PRIMARY KEY,
                        label string UNIQUE
                    )""");

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS products (
                        id integer PRIMARY KEY,
                        label string,
                        preis numeric,
                        categorie_id integer,
                        FOREIGN KEY (categorie_id) REFERENCES categories (id)
                            ON DELETE CASCADE ON UPDATE NO ACTION
                    )""");

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS rents (
                        id integer PRIMARY KEY,
                        c_id integer,
                        p_id integer,
                        status string,
                        date_from date,
                        date_to date,
                        FOREIGN KEY (c_id) REFERENCES clients (id)
                            ON DELETE CASCADE ON UPDATE NO ACTION,
                        FOREIGN KEY (p_id) REFERENCES products (id)
                            ON DELETE CASCADE ON UPDATE NO ACTION
                    )""");

            statement.executeUpdate("""
                    INSERT OR IGNORE INTO categories (label) VALUES
                        ('Technik'),
                        ('Beauty & Drogerie'),
                        ('Elektronik & Computer'),
                        ('Sport & Freizeit')""");

        } catch (SQLException e) {
            throw new DataAccessException("Failed to initialise database schema", e);
        }
    }
}
