package com.ahmedsghaier.rental.ui.dialog;

import com.ahmedsghaier.rental.domain.Customer;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Modal form for creating or editing a {@link Customer}.
 *
 * <p>Returns the populated (but not yet persisted) customer when the user confirms, or an
 * empty result when cancelled. Persistence and validation are the caller's responsibility
 * via the service layer.</p>
 */
public class CustomerDialog extends Dialog<Customer> {

    public CustomerDialog(Customer existing) {
        boolean editing = existing != null && !existing.isNew();
        Customer customer = existing != null ? existing : new Customer();

        setTitle(editing ? "Kunde bearbeiten" : "Neuer Kunde");
        setHeaderText(editing ? "Kundendaten bearbeiten" : "Neuen Kunden anlegen");
        Dialogs.applyTheme(getDialogPane());
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField firstName = field(customer.getFirstName(), "Vorname");
        TextField lastName = field(customer.getLastName(), "Nachname");
        TextField address = field(customer.getAddress(), "Anschrift");
        TextField postalCode = field(customer.getPostalCode(), "PLZ");
        TextField city = field(customer.getCity(), "Ort");
        TextField phone = field(customer.getPhone(), "Telefon");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16, 4, 4, 4));
        addRow(grid, 0, "Vorname", firstName);
        addRow(grid, 1, "Nachname", lastName);
        addRow(grid, 2, "Anschrift", address);
        addRow(grid, 3, "PLZ", postalCode);
        addRow(grid, 4, "Ort", city);
        addRow(grid, 5, "Telefon", phone);
        getDialogPane().setContent(grid);

        setResultConverter(button -> {
            if (button != ButtonType.OK) {
                return null;
            }
            customer.setFirstName(firstName.getText().trim());
            customer.setLastName(lastName.getText().trim());
            customer.setAddress(address.getText().trim());
            customer.setPostalCode(postalCode.getText().trim());
            customer.setCity(city.getText().trim());
            customer.setPhone(phone.getText().trim());
            return customer;
        });
    }

    private TextField field(String value, String prompt) {
        TextField textField = new TextField(value == null ? "" : value);
        textField.setPromptText(prompt);
        textField.setPrefWidth(260);
        return textField;
    }

    private void addRow(GridPane grid, int row, String label, TextField field) {
        grid.add(new Label(label), 0, row);
        grid.add(field, 1, row);
    }
}
