package com.ahmedsghaier.rental.ui.view;

import com.ahmedsghaier.rental.config.AppContext;
import com.ahmedsghaier.rental.domain.Category;
import com.ahmedsghaier.rental.domain.Product;
import com.ahmedsghaier.rental.domain.ProductAvailability;
import com.ahmedsghaier.rental.domain.exception.ValidationException;
import com.ahmedsghaier.rental.service.CategoryService;
import com.ahmedsghaier.rental.service.ProductService;
import com.ahmedsghaier.rental.ui.Styles;
import com.ahmedsghaier.rental.ui.View;
import com.ahmedsghaier.rental.ui.component.Widgets;
import com.ahmedsghaier.rental.ui.dialog.Dialogs;
import com.ahmedsghaier.rental.ui.dialog.ProductDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;

/**
 * Screen for managing the product catalogue, showing each product's live availability and
 * offering search plus category / status filters.
 */
public class ProductsView implements View {

    private static final String ALL_CATEGORIES = "Alle Kategorien";
    private static final String ALL_STATUSES = "Alle";
    private static final String STATUS_AVAILABLE = "Verfügbar";
    private static final String STATUS_RENTED = "Ausgeliehen";

    private final ProductService productService;
    private final CategoryService categoryService;

    private final ObservableList<ProductAvailability> products = FXCollections.observableArrayList();
    private final FilteredList<ProductAvailability> filtered = new FilteredList<>(products, p -> true);
    private final TableView<ProductAvailability> table = new TableView<>(filtered);

    private String searchText = "";
    private Category categoryFilter;
    private String statusFilter = ALL_STATUSES;

    public ProductsView(AppContext context) {
        this.productService = context.productService();
        this.categoryService = context.categoryService();
    }

    @Override
    public Region getRoot() {
        VBox header = Widgets.pageHeader("Produkte", "Produktkatalog und Verfügbarkeit");

        TextField search = new TextField();
        search.setPromptText("Produkt suchen …");
        search.getStyleClass().add(Styles.SEARCH_FIELD);
        search.textProperty().addListener((obs, old, value) -> {
            searchText = value;
            applyFilter();
        });

        ComboBox<Category> categoryBox = new ComboBox<>();
        categoryBox.getItems().add(new Category(-1, ALL_CATEGORIES));
        categoryBox.getItems().addAll(categoryService.findAll());
        categoryBox.getSelectionModel().selectFirst();
        categoryBox.valueProperty().addListener((obs, old, value) -> {
            categoryFilter = (value == null || value.getId() == -1) ? null : value;
            applyFilter();
        });

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(ALL_STATUSES, STATUS_AVAILABLE, STATUS_RENTED);
        statusBox.getSelectionModel().selectFirst();
        statusBox.valueProperty().addListener((obs, old, value) -> {
            statusFilter = value;
            applyFilter();
        });

        Button add = Widgets.primaryButton("＋ Neues Produkt");
        add.setOnAction(e -> openForm(null));

        Button edit = Widgets.secondaryButton("Bearbeiten");
        edit.setOnAction(e -> openForm(selectedProduct()));
        edit.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        Button delete = Widgets.dangerButton("Löschen");
        delete.setOnAction(e -> delete(selectedProduct()));
        delete.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        HBox toolbar = new HBox(10, search, categoryBox, statusBox,
                Widgets.grow(), edit, delete, add);

        configureTable();
        reload();

        VBox root = new VBox(20, header, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    private void configureTable() {
        TableColumn<ProductAvailability, String> nameCol = new TableColumn<>("Produkt");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLabel()));

        TableColumn<ProductAvailability, String> categoryCol = new TableColumn<>("Kategorie");
        categoryCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategoryLabel()));

        TableColumn<ProductAvailability, String> priceCol = new TableColumn<>("€ / Tag");
        priceCol.setCellValueFactory(d ->
                new SimpleStringProperty(Widgets.money(d.getValue().getDailyPrice())));

        TableColumn<ProductAvailability, ProductAvailability> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ProductAvailability item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : Widgets.statusPill(item.isAvailable()));
            }
        });

        table.getColumns().setAll(List.of(nameCol, categoryCol, priceCol, statusCol));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(placeholder("Keine Produkte gefunden."));
    }

    private void reload() {
        products.setAll(productService.findAllWithAvailability());
    }

    private void applyFilter() {
        filtered.setPredicate(this::matches);
    }

    private boolean matches(ProductAvailability product) {
        if (searchText != null && !searchText.isBlank()
                && !product.getLabel().toLowerCase(Locale.ROOT)
                        .contains(searchText.toLowerCase(Locale.ROOT))) {
            return false;
        }
        if (categoryFilter != null
                && product.product().getCategory().getId() != categoryFilter.getId()) {
            return false;
        }
        if (STATUS_AVAILABLE.equals(statusFilter) && !product.isAvailable()) {
            return false;
        }
        return !STATUS_RENTED.equals(statusFilter) || !product.isAvailable();
    }

    private Product selectedProduct() {
        ProductAvailability selected = table.getSelectionModel().getSelectedItem();
        return selected == null ? null : selected.product();
    }

    private void openForm(Product product) {
        new ProductDialog(product, categoryService.findAll()).showAndWait().ifPresent(edited -> {
            try {
                productService.save(edited);
                reload();
            } catch (ValidationException ex) {
                Dialogs.warn(ex.getMessage());
            }
        });
    }

    private void delete(Product product) {
        if (product == null) {
            return;
        }
        if (Dialogs.confirm("Produkt löschen",
                "Möchten Sie das Produkt \"" + product.getLabel() + "\" wirklich löschen?")) {
            productService.delete(product);
            reload();
        }
    }

    private Label placeholder(String text) {
        Label label = new Label(text);
        label.getStyleClass().add(Styles.MUTED);
        return label;
    }
}
