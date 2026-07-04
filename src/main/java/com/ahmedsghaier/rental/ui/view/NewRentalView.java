package com.ahmedsghaier.rental.ui.view;

import com.ahmedsghaier.rental.config.AppContext;
import com.ahmedsghaier.rental.domain.Customer;
import com.ahmedsghaier.rental.domain.ProductAvailability;
import com.ahmedsghaier.rental.domain.Rental;
import com.ahmedsghaier.rental.domain.exception.ValidationException;
import com.ahmedsghaier.rental.service.CustomerService;
import com.ahmedsghaier.rental.service.ProductService;
import com.ahmedsghaier.rental.service.RentalService;
import com.ahmedsghaier.rental.ui.Navigator;
import com.ahmedsghaier.rental.ui.Styles;
import com.ahmedsghaier.rental.ui.View;
import com.ahmedsghaier.rental.ui.component.Widgets;
import com.ahmedsghaier.rental.ui.dialog.CustomerPickerDialog;
import com.ahmedsghaier.rental.ui.dialog.Dialogs;
import com.ahmedsghaier.rental.ui.dialog.ProductPickerDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * New-rental workflow: choose a customer, add one or more available products with rental
 * periods to a basket, watch the total update live, then save and/or generate an invoice.
 */
public class NewRentalView implements View {

    private final CustomerService customerService;
    private final ProductService productService;
    private final RentalService rentalService;
    private final Navigator navigator;

    private final ObservableList<Rental> basket = FXCollections.observableArrayList();
    private final TableView<Rental> table = new TableView<>(basket);
    private final Label customerLabel = new Label("Kein Kunde ausgewählt");
    private final Label totalLabel = new Label(Widgets.money(null));

    private Customer customer;
    private Button addProduct;
    private Button save;
    private Button invoice;
    private boolean saved;

    public NewRentalView(AppContext context, Navigator navigator) {
        this.customerService = context.customerService();
        this.productService = context.productService();
        this.rentalService = context.rentalService();
        this.navigator = navigator;
    }

    @Override
    public Region getRoot() {
        VBox header = Widgets.pageHeader("Ausleihe", "Produkte an einen Kunden verleihen");

        Button pickCustomer = Widgets.secondaryButton("Kunde auswählen");
        pickCustomer.setOnAction(e -> pickCustomer());
        customerLabel.getStyleClass().add(Styles.SECTION_TITLE);
        HBox customerRow = new HBox(16, customerLabel, Widgets.grow(), pickCustomer);
        customerRow.setStyle("-fx-alignment: center-left;");

        configureTable();

        addProduct = Widgets.secondaryButton("＋ Produkt hinzufügen");
        addProduct.setOnAction(e -> addProduct());
        addProduct.setDisable(true);

        Button removeProduct = Widgets.dangerButton("Entfernen");
        removeProduct.setOnAction(e -> removeSelected());
        removeProduct.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        HBox basketActions = new HBox(10, addProduct, removeProduct);

        Label totalCaption = new Label("Gesamtsumme");
        totalCaption.getStyleClass().add(Styles.SECTION_TITLE);
        totalLabel.getStyleClass().add(Styles.RECEIPT_TOTAL);
        HBox totalRow = new HBox(12, Widgets.grow(), totalCaption, totalLabel);
        totalRow.setStyle("-fx-alignment: center-right;");

        VBox card = new VBox(16, customerRow, table, basketActions, totalRow);
        card.getStyleClass().add(Styles.CARD);
        VBox.setVgrow(table, Priority.ALWAYS);

        save = Widgets.secondaryButton("Speichern");
        save.setOnAction(e -> save());
        invoice = Widgets.primaryButton("Quittung erstellen");
        invoice.setOnAction(e -> createInvoice());
        updateActionState();
        HBox footer = new HBox(10, Widgets.grow(), save, invoice);

        VBox root = new VBox(20, header, card, footer);
        VBox.setVgrow(card, Priority.ALWAYS);
        return root;
    }

    private void configureTable() {
        table.getColumns().setAll(List.of(
                column("Produkt", Rental::getProductLabel),
                column("€ / Tag", r -> Widgets.money(r.getDailyPrice())),
                column("Tage", r -> String.valueOf(r.getDays())),
                column("Leihdatum", r -> Widgets.date(r.getDateFrom())),
                column("Rückgabedatum", r -> Widgets.date(r.getDateTo())),
                column("Summe", r -> Widgets.money(r.getTotal()))));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Label placeholder = new Label("Noch keine Produkte hinzugefügt.");
        placeholder.getStyleClass().add(Styles.MUTED);
        table.setPlaceholder(placeholder);
    }

    private void pickCustomer() {
        List<Customer> candidates = customerService.findAll();
        if (candidates.isEmpty()) {
            Dialogs.warn("Bitte legen Sie zuerst einen Kunden an.");
            return;
        }
        new CustomerPickerDialog(candidates).showAndWait().ifPresent(picked -> {
            customer = picked;
            customerLabel.setText(picked.getFullName().trim());
            addProduct.setDisable(false);
            updateActionState();
        });
    }

    private void addProduct() {
        List<ProductAvailability> available = new ArrayList<>(productService.findAvailable());
        available.removeIf(p -> basket.stream().anyMatch(r -> r.getProductId() == p.getProductId()));
        if (available.isEmpty()) {
            Dialogs.warn("Es sind derzeit keine weiteren Produkte verfügbar.");
            return;
        }
        new ProductPickerDialog(customer.getId(), available).showAndWait().ifPresent(rental -> {
            basket.add(rental);
            refreshTotal();
            updateActionState();
        });
    }

    private void removeSelected() {
        Rental selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            basket.remove(selected);
            refreshTotal();
            updateActionState();
        }
    }

    private void save() {
        if (persist()) {
            Dialogs.warn("Ausleihe gespeichert.");
        }
    }

    private void createInvoice() {
        if (persist()) {
            navigator.show(new InvoiceView(customer, new ArrayList<>(basket),
                    rentalService, navigator).getRoot());
        }
    }

    /** Persists the basket once; returns {@code true} if the data is safely stored. */
    private boolean persist() {
        if (saved) {
            return true;
        }
        try {
            rentalService.checkout(new ArrayList<>(basket));
            saved = true;
            save.setDisable(true);
            addProduct.setDisable(true);
            return true;
        } catch (ValidationException ex) {
            Dialogs.warn(ex.getMessage());
            return false;
        }
    }

    private void refreshTotal() {
        totalLabel.setText(Widgets.money(rentalService.computeTotal(basket)));
    }

    private void updateActionState() {
        boolean ready = customer != null && !basket.isEmpty() && !saved;
        save.setDisable(!ready);
        invoice.setDisable(customer == null || basket.isEmpty());
    }

    private TableColumn<Rental, String> column(String title, Function<Rental, String> getter) {
        TableColumn<Rental, String> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
    }
}
