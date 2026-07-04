package com.ahmedsghaier.rental.ui.view;

import com.ahmedsghaier.rental.config.AppContext;
import com.ahmedsghaier.rental.domain.Customer;
import com.ahmedsghaier.rental.domain.exception.ValidationException;
import com.ahmedsghaier.rental.service.CustomerService;
import com.ahmedsghaier.rental.ui.Styles;
import com.ahmedsghaier.rental.ui.View;
import com.ahmedsghaier.rental.ui.component.Widgets;
import com.ahmedsghaier.rental.ui.dialog.CustomerDialog;
import com.ahmedsghaier.rental.ui.dialog.Dialogs;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Screen for managing customers: searchable list with create, edit and delete actions.
 */
public class CustomersView implements View {

    private final CustomerService customerService;
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();
    private final FilteredList<Customer> filtered = new FilteredList<>(customers, c -> true);
    private final TableView<Customer> table = new TableView<>(filtered);

    public CustomersView(AppContext context) {
        this.customerService = context.customerService();
    }

    @Override
    public Region getRoot() {
        VBox header = Widgets.pageHeader("Kunden", "Kundenstamm verwalten");

        TextField search = new TextField();
        search.setPromptText("Kunde suchen …");
        search.getStyleClass().add(Styles.SEARCH_FIELD);
        search.textProperty().addListener((obs, old, value) ->
                filtered.setPredicate(c -> matches(c, value)));

        Button add = Widgets.primaryButton("＋ Neuer Kunde");
        add.setOnAction(e -> openForm(null));

        Button edit = Widgets.secondaryButton("Bearbeiten");
        edit.setOnAction(e -> openForm(table.getSelectionModel().getSelectedItem()));
        edit.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        Button delete = Widgets.dangerButton("Löschen");
        delete.setOnAction(e -> delete(table.getSelectionModel().getSelectedItem()));
        delete.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        HBox toolbar = new HBox(10, search, Widgets.grow(), edit, delete, add);
        toolbar.setSpacing(10);

        configureTable();
        reload();

        VBox root = new VBox(20, header, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    private void configureTable() {
        table.getColumns().setAll(List.of(
                column("Vorname", Customer::getFirstName),
                column("Nachname", Customer::getLastName),
                column("Anschrift", Customer::getAddress),
                column("PLZ", Customer::getPostalCode),
                column("Ort", Customer::getCity),
                column("Telefon", Customer::getPhone)));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(placeholder("Noch keine Kunden vorhanden."));
    }

    private void reload() {
        customers.setAll(customerService.findAll());
    }

    private void openForm(Customer customer) {
        new CustomerDialog(customer).showAndWait().ifPresent(edited -> {
            try {
                customerService.save(edited);
                reload();
            } catch (ValidationException ex) {
                Dialogs.warn(ex.getMessage());
            }
        });
    }

    private void delete(Customer customer) {
        if (customer == null) {
            return;
        }
        if (Dialogs.confirm("Kunde löschen",
                "Möchten Sie den Kunden \"" + customer.getFullName().trim() + "\" wirklich löschen?")) {
            customerService.delete(customer);
            reload();
        }
    }

    private boolean matches(Customer customer, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String needle = query.toLowerCase(Locale.ROOT);
        return customer.getFullName().toLowerCase(Locale.ROOT).contains(needle)
                || (customer.getCity() != null
                    && customer.getCity().toLowerCase(Locale.ROOT).contains(needle));
    }

    private TableColumn<Customer, String> column(String title, Function<Customer, String> getter) {
        TableColumn<Customer, String> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
    }

    private Label placeholder(String text) {
        Label label = new Label(text);
        label.getStyleClass().add(Styles.MUTED);
        return label;
    }
}
