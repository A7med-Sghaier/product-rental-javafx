package com.ahmedsghaier.rental.ui.dialog;

import com.ahmedsghaier.rental.domain.Customer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;

/**
 * Modal picker that lets the user choose one {@link Customer} from a searchable list.
 *
 * <p>Returns the selected customer on OK, or {@code null} if cancelled or nothing selected.</p>
 */
public class CustomerPickerDialog extends Dialog<Customer> {

    public CustomerPickerDialog(List<Customer> customers) {
        setTitle("Kunde auswählen");
        setHeaderText("Kunde auswählen");
        Dialogs.applyTheme(getDialogPane());
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ObservableList<Customer> source = FXCollections.observableArrayList(customers);
        FilteredList<Customer> filtered = new FilteredList<>(source, c -> true);

        TextField search = new TextField();
        search.setPromptText("Kunde suchen …");
        search.textProperty().addListener((obs, old, value) ->
                filtered.setPredicate(c -> matches(c, value)));

        TableView<Customer> table = new TableView<>(filtered);
        table.setPrefSize(460, 320);
        table.getColumns().addAll(List.of(
                column("Vorname", Customer::getFirstName),
                column("Nachname", Customer::getLastName),
                column("Ort", Customer::getCity)));

        VBox content = new VBox(10, search, table);
        content.setPadding(new Insets(12, 4, 4, 4));
        getDialogPane().setContent(content);

        setResultConverter(button ->
                button == ButtonType.OK ? table.getSelectionModel().getSelectedItem() : null);
    }

    private boolean matches(Customer customer, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        return customer.getFullName().toLowerCase(Locale.ROOT)
                .contains(query.toLowerCase(Locale.ROOT));
    }

    private TableColumn<Customer, String> column(String title,
                                                 java.util.function.Function<Customer, String> getter) {
        TableColumn<Customer, String> col = new TableColumn<>(title);
        col.setCellValueFactory(data ->
                new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
    }
}
