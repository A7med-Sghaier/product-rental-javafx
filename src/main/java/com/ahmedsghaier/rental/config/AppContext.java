package com.ahmedsghaier.rental.config;

import com.ahmedsghaier.rental.persistence.ConnectionFactory;
import com.ahmedsghaier.rental.persistence.SchemaInitializer;
import com.ahmedsghaier.rental.persistence.jdbc.JdbcCategoryRepository;
import com.ahmedsghaier.rental.persistence.jdbc.JdbcCustomerRepository;
import com.ahmedsghaier.rental.persistence.jdbc.JdbcProductRepository;
import com.ahmedsghaier.rental.persistence.jdbc.JdbcRentalRepository;
import com.ahmedsghaier.rental.service.CategoryService;
import com.ahmedsghaier.rental.service.CustomerService;
import com.ahmedsghaier.rental.service.ProductService;
import com.ahmedsghaier.rental.service.RentalService;

/**
 * Composition root: builds and wires the application's object graph.
 *
 * <p>This is the single place where concrete implementations are chosen (JDBC
 * repositories over an SQLite {@link ConnectionFactory}), keeping the rest of the code
 * dependent only on interfaces. The UI receives fully-constructed services and never
 * instantiates repositories itself.</p>
 */
public class AppContext {

    private final CustomerService customerService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final RentalService rentalService;

    /** Builds a context against the default on-disk SQLite database. */
    public AppContext() {
        this(ConnectionFactory.ofDefault());
    }

    /**
     * Builds a context against a specific database, initialising its schema.
     *
     * @param connectionFactory the database to use (e.g. an in-memory one for tests)
     */
    public AppContext(ConnectionFactory connectionFactory) {
        new SchemaInitializer(connectionFactory).initialize();

        var customerRepository = new JdbcCustomerRepository(connectionFactory);
        var categoryRepository = new JdbcCategoryRepository(connectionFactory);
        var productRepository = new JdbcProductRepository(connectionFactory);
        var rentalRepository = new JdbcRentalRepository(connectionFactory);

        this.customerService = new CustomerService(customerRepository);
        this.categoryService = new CategoryService(categoryRepository);
        this.productService = new ProductService(productRepository);
        this.rentalService = new RentalService(rentalRepository);
    }

    public CustomerService customerService() {
        return customerService;
    }

    public CategoryService categoryService() {
        return categoryService;
    }

    public ProductService productService() {
        return productService;
    }

    public RentalService rentalService() {
        return rentalService;
    }
}
