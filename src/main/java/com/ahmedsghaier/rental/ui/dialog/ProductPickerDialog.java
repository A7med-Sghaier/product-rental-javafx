package com.ahmedsghaier.rental.ui.dialog;

import com.ahmedsghaier.rental.domain.ProductAvailability;
import com.ahmedsghaier.rental.domain.Rental;
import com.ahmedsghaier.rental.ui.component.Widgets;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DateCell;
import javafx.scene.control.Dialog;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

/**
 * Modal picker for adding a product to a rental basket: the user selects an available
 * product and a rental period.
 *
 * <p>On confirmation the dialog returns a fully-built {@link Rental} for the given customer;
 * it returns {@code null} if cancelled or if no product was selected. The return-date picker
 * disables any day on or before the selected start date.</p>
 */
public class ProductPickerDialog extends Dialog<Rental> {

    public ProductPickerDialog(int customerId, List<ProductAvailability> available) {
        setTitle("Produkt hinzufügen");
        setHeaderText("Produkt und Zeitraum wählen");
        Dialogs.applyTheme(getDialogPane());
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ObservableList<ProductAvailability> source = FXCollections.observableArrayList(available);
        FilteredList<ProductAvailability> filtered = new FilteredList<>(source, p -> true);

        TextField search = new TextField();
        search.setPromptText("Produkt suchen …");
        search.textProperty().addListener((obs, old, value) ->
                filtered.setPredicate(p -> matches(p, value)));

        TableView<ProductAvailability> table = new TableView<>(filtered);
        table.setPrefSize(500, 260);
        TableColumn<ProductAvailability, String> nameCol = new TableColumn<>("Produkt");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLabel()));
        TableColumn<ProductAvailability, String> catCol = new TableColumn<>("Kategorie");
        catCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategoryLabel()));
        TableColumn<ProductAvailability, String> priceCol = new TableColumn<>("€ / Tag");
        priceCol.setCellValueFactory(d ->
                new SimpleStringProperty(Widgets.money(d.getValue().getDailyPrice())));
        table.getColumns().addAll(List.of(nameCol, catCol, priceCol));

        DatePicker from = new DatePicker(LocalDate.now());
        DatePicker to = new DatePicker(LocalDate.now().plus(7, ChronoUnit.DAYS));
        restrictReturnDates(from, to);

        HBox dates = new HBox(10,
                new Label("Leihdatum"), from,
                new Label("Rückgabedatum"), to);
        dates.setPadding(new Insets(6, 0, 0, 0));

        VBox content = new VBox(10, search, table, dates);
        content.setPadding(new Insets(12, 4, 4, 4));
        getDialogPane().setContent(content);

        setResultConverter(button -> {
            if (button != ButtonType.OK) {
                return null;
            }
            ProductAvailability selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return null;
            }
            return Rental.of(customerId, selected.product(), from.getValue(), to.getValue());
        });
    }

    private boolean matches(ProductAvailability product, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        return product.getLabel().toLowerCase(Locale.ROOT)
                .contains(query.toLowerCase(Locale.ROOT));
    }

    /** Keeps the return date strictly after the (possibly changing) start date. */
    private void restrictReturnDates(DatePicker from, DatePicker to) {
        to.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                LocalDate start = from.getValue();
                if (item != null && start != null && !item.isAfter(start)) {
                    setDisable(true);
                }
            }
        });
        from.valueProperty().addListener((obs, old, value) -> {
            if (value != null && (to.getValue() == null || !to.getValue().isAfter(value))) {
                to.setValue(value.plusDays(1));
            }
        });
    }
}
