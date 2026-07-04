package com.ahmedsghaier.rental.devtools;

import com.ahmedsghaier.rental.config.AppContext;
import com.ahmedsghaier.rental.domain.Category;
import com.ahmedsghaier.rental.domain.Customer;
import com.ahmedsghaier.rental.domain.Product;
import com.ahmedsghaier.rental.domain.Rental;
import com.ahmedsghaier.rental.service.CategoryService;
import com.ahmedsghaier.rental.service.CustomerService;
import com.ahmedsghaier.rental.service.ProductService;
import com.ahmedsghaier.rental.service.RentalService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Development utility that seeds the local {@code Laiheus.db} database with realistic demo
 * data (customers, products and active rentals) so the application shows meaningful content
 * for screenshots and manual exploration.
 *
 * <p>This is <strong>not</strong> part of the application itself — it is a one-off tool that
 * writes through the normal service layer. Run it with:</p>
 *
 * <pre>{@code
 * mvn -q compile exec:java -Dexec.mainClass=com.ahmedsghaier.rental.devtools.SampleDataLoader
 * }</pre>
 *
 * <p>It is safe by default: if the database already contains customers it does nothing,
 * unless {@code --force} is passed to add the demo data anyway.</p>
 */
public final class SampleDataLoader {

    private final CustomerService customerService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final RentalService rentalService;

    private SampleDataLoader(AppContext context) {
        this.customerService = context.customerService();
        this.categoryService = context.categoryService();
        this.productService = context.productService();
        this.rentalService = context.rentalService();
    }

    public static void main(String[] args) {
        boolean force = args.length > 0 && "--force".equals(args[0]);
        SampleDataLoader loader = new SampleDataLoader(new AppContext());

        if (!force && !loader.customerService.findAll().isEmpty()) {
            System.out.println("Database already contains customers — nothing seeded. "
                    + "Pass --force to add demo data anyway.");
            return;
        }
        loader.seed();
        System.out.println("Demo data written to Laiheus.db. Launch with: mvn javafx:run");
    }

    /**
     * Seeds the demo data only if the database has no customers yet. Useful for tooling
     * (such as the screenshot generator) that needs a populated database to render.
     *
     * @param context the application context to seed
     */
    public static void seedIfEmpty(AppContext context) {
        SampleDataLoader loader = new SampleDataLoader(context);
        if (loader.customerService.findAll().isEmpty()) {
            loader.seed();
        }
    }

    private void seed() {
        Map<String, Category> categories = categoriesByLabel();

        List<Customer> customers = List.of(
                customer("Anna", "Müller", "Kastanienallee 12", "10435", "Berlin", "030 1234567"),
                customer("Lukas", "Schmidt", "Rothenbaumchaussee 8", "20148", "Hamburg", "040 2345678"),
                customer("Sophie", "Weber", "Leopoldstraße 45", "80802", "München", "089 3456789"),
                customer("Jonas", "Fischer", "Ehrenstraße 22", "50672", "Köln", "0221 4567890"),
                customer("Marie", "Wagner", "Berger Straße 91", "60316", "Frankfurt", "069 5678901"),
                customer("Felix", "Becker", "Königstraße 17", "70173", "Stuttgart", "0711 6789012"),
                customer("Laura", "Hoffmann", "Nordstraße 5", "40477", "Düsseldorf", "0211 7890123"),
                customer("Max", "Schäfer", "Karl-Liebknecht-Str. 3", "04107", "Leipzig", "0341 8901234"));

        Map<String, Product> products = new HashMap<>();
        saveProducts(products, categories.get("Technik"),
                product("Bohrmaschine Bosch", "8.50"),
                product("Akkuschrauber Makita", "6.90"),
                product("Winkelschleifer", "7.50"),
                product("Hochdruckreiniger Kärcher", "14.00"));
        saveProducts(products, categories.get("Elektronik & Computer"),
                product("Beamer Full-HD", "19.90"),
                product("MacBook Pro 14\"", "29.00"),
                product("Spiegelreflexkamera Canon", "22.50"),
                product("Drohne DJI Mini", "24.00"));
        saveProducts(products, categories.get("Beauty & Drogerie"),
                product("Haartrockner Profi", "4.50"),
                product("Kosmetikspiegel LED", "3.90"));
        saveProducts(products, categories.get("Sport & Freizeit"),
                product("Mountainbike 27,5\"", "16.00"),
                product("Zelt 4 Personen", "12.50"),
                product("Stand-Up-Paddle Board", "18.00"),
                product("Skiausrüstung komplett", "21.00"));

        LocalDate today = LocalDate.now();
        rent(customers.get(0), today.minusDays(2), today.plusDays(5),
                products.get("Beamer Full-HD"), products.get("MacBook Pro 14\""));
        rent(customers.get(1), today.minusDays(1), today.plusDays(6),
                products.get("Bohrmaschine Bosch"), products.get("Hochdruckreiniger Kärcher"));
        rent(customers.get(2), today, today.plusDays(3),
                products.get("Spiegelreflexkamera Canon"));
        rent(customers.get(3), today.minusDays(4), today.plusDays(2),
                products.get("Mountainbike 27,5\""), products.get("Zelt 4 Personen"));
        rent(customers.get(4), today.minusDays(3), today.plusDays(4),
                products.get("Drohne DJI Mini"));
        rent(customers.get(6), today, today.plusDays(7),
                products.get("Skiausrüstung komplett"), products.get("Stand-Up-Paddle Board"));
    }

    private Map<String, Category> categoriesByLabel() {
        Map<String, Category> byLabel = new HashMap<>();
        for (Category category : categoryService.findAll()) {
            byLabel.put(category.getLabel(), category);
        }
        return byLabel;
    }

    private void saveProducts(Map<String, Product> target, Category category, Product... drafts) {
        for (Product draft : drafts) {
            draft.setCategory(category);
            Product saved = productService.save(draft);
            target.put(saved.getLabel(), saved);
        }
    }

    private void rent(Customer customer, LocalDate from, LocalDate to, Product... products) {
        List<Rental> basket = java.util.Arrays.stream(products)
                .map(product -> Rental.of(customer.getId(), product, from, to))
                .toList();
        rentalService.checkout(basket);
    }

    private Customer customer(String first, String last, String address,
                              String plz, String city, String phone) {
        Customer customer = new Customer(0, first, last, address, plz, city, phone);
        return customerService.save(customer);
    }

    private Product product(String label, String dailyPrice) {
        return new Product(0, label, new BigDecimal(dailyPrice), null);
    }
}
