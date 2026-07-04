package com.ahmedsghaier.rental.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

/**
 * Base class for repository integration tests.
 *
 * <p>Each test method runs against a fresh, throwaway SQLite database created in a JUnit
 * {@link TempDir}. A file-based database is used rather than {@code :memory:} because the
 * repositories open a new connection per call, which would otherwise see a different empty
 * in-memory database every time.</p>
 */
abstract class RepositoryTestBase {

    @TempDir
    Path tempDir;

    protected ConnectionFactory connectionFactory;

    @BeforeEach
    void initSchema() {
        String url = "jdbc:sqlite:" + tempDir.resolve("rental-test.db");
        connectionFactory = new ConnectionFactory(url);
        new SchemaInitializer(connectionFactory).initialize();
    }
}
