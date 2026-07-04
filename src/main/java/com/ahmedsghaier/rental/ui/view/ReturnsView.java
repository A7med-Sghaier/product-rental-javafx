package com.ahmedsghaier.rental.ui.view;

import com.ahmedsghaier.rental.config.AppContext;
import com.ahmedsghaier.rental.domain.Customer;
import com.ahmedsghaier.rental.domain.Rental;
import com.ahmedsghaier.rental.domain.exception.ValidationException;
import com.ahmedsghaier.rental.service.CustomerService;
import com.ahmedsghaier.rental.service.RentalService;
import com.ahmedsghaier.rental.ui.Styles;
import com.ahmedsghaier.rental.ui.View;
import com.ahmedsghaier.rental.ui.component.Widgets;
import com.ahmedsghaier.rental.ui.dialog.CustomerPickerDialog;
import com.ahmedsghaier.rental.ui.dialog.Dialogs;
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

import java.util.List;
import java.util.function.Function;

/**
 * Returns workflow: pick a customer who has active rentals, then return individual
 * products from their list.
 */
public class ReturnsView implements View {

    private final CustomerService customerService;
    private final RentalService rentalService;

    private final ObservableList<Rental> rentals = FXCollections.observableArrayList();
    private final TableView<Rental> table = new TableView<>(rentals);
    private final Label customerLabel = new Label("Kein Kunde ausgewählt");

    private Customer selectedCustomer;

    public ReturnsView(AppContext context) {
        this.customerService = context.customerService();
        this.rentalService = context.rentalService();
    }

    @Override
    public Region getRoot() {
        VBox header = Widgets.pageHeader("Rückgabe", "Ausgeliehene Produkte zurücknehmen");

        Button pick = Widgets.primaryButton("Kunde auswählen");
        pick.setOnAction(e -> pickCustomer());

        customerLabel.getStyleClass().add(Styles.SECTION_TITLE);
        HBox customerRow = new HBox(16, customerLabel, Widgets.grow(), pick);
        customerRow.setStyle("-fx-alignment: center-left;");

        configureTable();

        Button returnBtn = Widgets.primaryButton("Rückgeben");
        returnBtn.setOnAction(e -> returnSelected());
        returnBtn.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        HBox footer = new HBox(returnBtn);
        footer.setStyle("-fx-alignment: center-right;");

        VBox card = new VBox(16, customerRow, table, footer);
        card.getStyleClass().add(Styles.CARD);
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox root = new VBox(20, header, card);
        VBox.setVgrow(card, Priority.ALWAYS);
        return root;
    }

    private void configureTable() {
        table.getColumns().setAll(List.of(
                column("Produkt", Rental::getProductLabel),
                column("€ / Tag", r -> Widgets.money(r.getDailyPrice())),
                column("Tage", r -> String.valueOf(r.getDays())),
                column("Leihdatum", r -> Widgets.date(r.getDateFrom())),
                column("Rückgabedatum", r -> Widgets.date(r.getDateTo()))));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Label placeholder = new Label("Bitte wählen Sie einen Kunden aus.");
        placeholder.getStyleClass().add(Styles.MUTED);
        table.setPlaceholder(placeholder);
    }

    private void pickCustomer() {
        List<Customer> candidates = customerService.findAllWithActiveRentals();
        if (candidates.isEmpty()) {
            Dialogs.warn("Es gibt derzeit keine Kunden mit aktiven Ausleihen.");
            return;
        }
        new CustomerPickerDialog(candidates).showAndWait().ifPresent(customer -> {
            selectedCustomer = customer;
            customerLabel.setText(customer.getFullName().trim());
            reloadRentals();
        });
    }

    private void reloadRentals() {
        if (selectedCustomer == null) {
            rentals.clear();
            return;
        }
        rentals.setAll(rentalService.findActiveByCustomerId(selectedCustomer.getId()));
    }

    private void returnSelected() {
        Rental rental = table.getSelectionModel().getSelectedItem();
        if (rental == null) {
            return;
        }
        if (Dialogs.confirm("Rückgabe bestätigen",
                "Produkt \"" + rental.getProductLabel() + "\" als zurückgegeben markieren?")) {
            try {
                rentalService.returnRental(rental);
                reloadRentals();
            } catch (ValidationException ex) {
                Dialogs.warn(ex.getMessage());
            }
        }
    }

    private TableColumn<Rental, String> column(String title, Function<Rental, String> getter) {
        TableColumn<Rental, String> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
    }
}
