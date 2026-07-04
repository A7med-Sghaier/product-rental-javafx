package com.ahmedsghaier.rental.service;

import com.ahmedsghaier.rental.domain.Category;
import com.ahmedsghaier.rental.domain.Product;
import com.ahmedsghaier.rental.domain.ProductAvailability;
import com.ahmedsghaier.rental.domain.RentalStatus;
import com.ahmedsghaier.rental.domain.exception.ValidationException;
import com.ahmedsghaier.rental.persistence.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private Product valid() {
        return new Product(0, "Kamera", new BigDecimal("9.90"), new Category(1, "Technik"));
    }

    @Test
    void savesValidProduct() {
        Product product = valid();
        when(repository.save(product)).thenReturn(product);

        assertEquals(product, service.save(product));
        verify(repository).save(product);
    }

    @Test
    void rejectsNegativePrice() {
        Product product = valid();
        product.setDailyPrice(new BigDecimal("-1"));

        assertThrows(ValidationException.class, () -> service.save(product));
        verify(repository, never()).save(product);
    }

    @Test
    void rejectsMissingCategory() {
        Product product = valid();
        product.setCategory(null);

        assertThrows(ValidationException.class, () -> service.save(product));
    }

    @Test
    void rejectsBlankLabel() {
        Product product = valid();
        product.setLabel("");

        assertThrows(ValidationException.class, () -> service.save(product));
    }

    @Test
    void findAvailableFiltersOutRentedProducts() {
        ProductAvailability free = new ProductAvailability(valid(), RentalStatus.AVAILABLE, null, null);
        ProductAvailability rented = new ProductAvailability(
                new Product(2, "Beamer", BigDecimal.TEN, new Category(1, "Technik")),
                RentalStatus.RENTED, null, null);
        when(repository.findAllWithAvailability()).thenReturn(List.of(free, rented));

        List<ProductAvailability> available = service.findAvailable();

        assertEquals(1, available.size());
        assertEquals("Kamera", available.get(0).getLabel());
    }

    @Test
    void deleteDelegatesToRepository() {
        Product product = valid();
        product.setId(42);

        service.delete(product);

        verify(repository).deleteById(42);
    }
}
