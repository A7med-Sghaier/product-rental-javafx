package com.ahmedsghaier.rental.ui.view;

import com.ahmedsghaier.rental.config.AppContext;
import com.ahmedsghaier.rental.domain.Rental;
import com.ahmedsghaier.rental.service.RentalService;
import com.ahmedsghaier.rental.ui.Navigator;
import com.ahmedsghaier.rental.ui.Styles;
import com.ahmedsghaier.rental.ui.View;
import com.ahmedsghaier.rental.ui.component.Widgets;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Landing screen: at-a-glance KPIs plus a filterable table of all active rentals.
 */
public class DashboardView implements View {

    private final RentalService rentalService;
    private final Navigator navigator;

    private final ObservableList<Rental> rentals = FXCollections.observableArrayList();
    private final FilteredList<Rental> filtered = new FilteredList<>(rentals, r -> true);
    private final TableView<Rental> table = new TableView<>(filtered);

    private String customerQuery = "";
    private String productQuery = "";
    private LocalDate from;
    private LocalDate to;

    public DashboardView(AppContext context, Navigator navigator) {
        this.rentalService = context.rentalService();
        this.navigator = navigator;
    }

    @Override
    public Region getRoot() {
        rentals.setAll(rentalService.findActive());

        VBox header = Widgets.pageHeader("Übersicht", "Aktuelle Ausleihen auf einen Blick");
        HBox kpis = buildKpis();
        HBox filters = buildFilters();
        configureTable();

        VBox root = new VBox(20, header, kpis, filters, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    private HBox buildKpis() {
        long activeRentals = rentals.size();
        long activeCustomers = rentals.stream().map(Rental::getCustomerId).distinct().count();
        String revenue = Widgets.money(rentalService.computeTotal(rentals));

        HBox kpis = new HBox(16,
                Widgets.kpiCard("Aktive Ausleihen", String.valueOf(activeRentals), Styles.KPI_ACCENT),
                Widgets.kpiCard("Aktive Kunden", String.valueOf(activeCustomers), null),
                Widgets.kpiCard("Offener Umsatz", revenue, Styles.KPI_SUCCESS));
        return kpis;
    }

    private HBox buildFilters() {
        TextField customer = new TextField();
        customer.setPromptText("Kunde suchen …");
        customer.getStyleClass().add(Styles.SEARCH_FIELD);
        customer.textProperty().addListener((obs, old, value) -> {
            customerQuery = value;
            applyFilter();
        });

        TextField product = new TextField();
        product.setPromptText("Produkt suchen …");
        product.textProperty().addListener((obs, old, value) -> {
            productQuery = value;
            applyFilter();
        });

        DatePicker fromPicker = new DatePicker();
        fromPicker.setPromptText("von");
        fromPicker.valueProperty().addListener((obs, old, value) -> {
            from = value;
            applyFilter();
        });

        DatePicker toPicker = new DatePicker();
        toPicker.setPromptText("bis");
        toPicker.valueProperty().addListener((obs, old, value) -> {
            to = value;
            applyFilter();
        });

        Button toReturns = Widgets.secondaryButton("Zur Rückgabe →");
        toReturns.setOnAction(e -> navigator.showReturns());

        HBox filters = new HBox(10,
                customer, product,
                new Label("Zeitraum"), fromPicker, toPicker,
                Widgets.grow(), toReturns);
        filters.setStyle("-fx-alignment: center-left;");
        return filters;
    }

    private void configureTable() {
        table.getColumns().setAll(List.of(
                textColumn("Kunde", Rental::getCustomerName),
                textColumn("Produkt", Rental::getProductLabel),
                textColumn("€ / Tag", r -> Widgets.money(r.getDailyPrice())),
                textColumn("Tage", r -> String.valueOf(r.getDays())),
                textColumn("Leihdatum", r -> Widgets.date(r.getDateFrom())),
                textColumn("Rückgabedatum", r -> Widgets.date(r.getDateTo())),
                textColumn("Summe", r -> Widgets.money(r.getTotal()))));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Label placeholder = new Label("Keine aktiven Ausleihen.");
        placeholder.getStyleClass().add(Styles.MUTED);
        table.setPlaceholder(placeholder);
    }

    private void applyFilter() {
        filtered.setPredicate(this::matches);
    }

    private boolean matches(Rental rental) {
        if (!contains(rental.getCustomerName(), customerQuery)) {
            return false;
        }
        if (!contains(rental.getProductLabel(), productQuery)) {
            return false;
        }
        if (from != null && (rental.getDateFrom() == null || rental.getDateFrom().isBefore(from))) {
            return false;
        }
        return to == null || (rental.getDateTo() != null && !rental.getDateTo().isAfter(to));
    }

    private boolean contains(String value, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        return value != null && value.toLowerCase(Locale.ROOT)
                .contains(query.toLowerCase(Locale.ROOT));
    }

    private TableColumn<Rental, String> textColumn(String title, Function<Rental, String> getter) {
        TableColumn<Rental, String> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
    }
}
