package com.ahmedsghaier.rental.persistence;

import com.ahmedsghaier.rental.domain.Category;
import com.ahmedsghaier.rental.domain.Product;
import com.ahmedsghaier.rental.domain.ProductAvailability;
import com.ahmedsghaier.rental.persistence.jdbc.JdbcCategoryRepository;
import com.ahmedsghaier.rental.persistence.jdbc.JdbcProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcProductRepositoryTest extends RepositoryTestBase {

    private JdbcProductRepository products;
    private Category category;

    @BeforeEach
    void setUp() {
        products = new JdbcProductRepository(connectionFactory);
        // The default categories are seeded by the schema initializer.
        category = new JdbcCategoryRepository(connectionFactory).findAll().get(0);
    }

    private Product sample(String label, String price) {
        return new Product(0, label, new BigDecimal(price), category);
    }

    @Test
    void insertAssignsIdAndPersistsPrice() {
        Product saved = products.save(sample("Bohrmaschine", "9.90"));

        assertTrue(saved.getId() > 0);
        Product reloaded = products.findAll().get(0);
        assertEquals(0, new BigDecimal("9.90").compareTo(reloaded.getDailyPrice()));
        assertEquals(category.getId(), reloaded.getCategory().getId());
    }

    @Test
    void availabilityMarksFreeProductsAsAvailable() {
        products.save(sample("Beamer", "12.00"));

        List<ProductAvailability> availability = products.findAllWithAvailability();

        assertEquals(1, availability.size());
        assertTrue(availability.get(0).isAvailable());
    }

    @Test
    void updateChangesLabelAndPrice() {
        Product saved = products.save(sample("Kamera", "5.00"));
        saved.setLabel("Kamera Pro");
        saved.setDailyPrice(new BigDecimal("7.50"));

        products.save(saved);

        Product reloaded = products.findAll().get(0);
        assertEquals("Kamera Pro", reloaded.getLabel());
        assertEquals(0, new BigDecimal("7.50").compareTo(reloaded.getDailyPrice()));
    }
}
