package com.ahmedsghaier.rental.ui.view;

import com.ahmedsghaier.rental.domain.Customer;
import com.ahmedsghaier.rental.domain.Rental;
import com.ahmedsghaier.rental.service.RentalService;
import com.ahmedsghaier.rental.ui.Navigator;
import com.ahmedsghaier.rental.ui.Styles;
import com.ahmedsghaier.rental.ui.View;
import com.ahmedsghaier.rental.ui.component.Widgets;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/**
 * Read-only receipt summarising a completed rental: customer, rented products and the
 * grand total.
 */
public class InvoiceView implements View {

    private final Customer customer;
    private final List<Rental> lines;
    private final RentalService rentalService;
    private final Navigator navigator;

    public InvoiceView(Customer customer, List<Rental> lines,
                       RentalService rentalService, Navigator navigator) {
        this.customer = customer;
        this.lines = lines;
        this.rentalService = rentalService;
        this.navigator = navigator;
    }

    @Override
    public Region getRoot() {
        VBox header = Widgets.pageHeader("Quittung", "Verleihbestätigung");

        Label receiptTitle = new Label("Verleihbestätigung / Quittung");
        receiptTitle.getStyleClass().add(Styles.SECTION_TITLE);

        Label customerCaption = new Label("Kunde");
        customerCaption.getStyleClass().add(Styles.FIELD_LABEL);
        Label customerName = new Label(customer.getFullName().trim());
        Label dateCaption = new Label("Datum");
        dateCaption.getStyleClass().add(Styles.FIELD_LABEL);
        Label date = new Label(Widgets.date(LocalDate.now()));
        HBox meta = new HBox(40,
                new VBox(2, customerCaption, customerName),
                new VBox(2, dateCaption, date));

        TableView<Rental> table = new TableView<>();
        table.getItems().setAll(lines);
        table.getColumns().setAll(List.of(
                column("Produkt", Rental::getProductLabel),
                column("€ / Tag", r -> Widgets.money(r.getDailyPrice())),
                column("Tage", r -> String.valueOf(r.getDays())),
                column("Leihdatum", r -> Widgets.date(r.getDateFrom())),
                column("Rückgabedatum", r -> Widgets.date(r.getDateTo())),
                column("Summe", r -> Widgets.money(r.getTotal()))));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label totalCaption = new Label("Gesamtsumme");
        totalCaption.getStyleClass().add(Styles.SECTION_TITLE);
        Label total = new Label(Widgets.money(rentalService.computeTotal(lines)));
        total.getStyleClass().add(Styles.RECEIPT_TOTAL);
        HBox totalRow = new HBox(12, Widgets.grow(), totalCaption, total);
        totalRow.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(18, receiptTitle, meta, table, totalRow);
        card.getStyleClass().add(Styles.CARD);
        VBox.setVgrow(table, Priority.ALWAYS);

        Button back = Widgets.primaryButton("Zur Übersicht");
        back.setOnAction(e -> navigator.showDashboard());
        HBox footer = new HBox(Widgets.grow(), back);

        VBox root = new VBox(20, header, card, footer);
        VBox.setVgrow(card, Priority.ALWAYS);
        return root;
    }

    private TableColumn<Rental, String> column(String title, Function<Rental, String> getter) {
        TableColumn<Rental, String> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
    }
}
