package com.ahmedsghaier.rental.ui.dialog;

import com.ahmedsghaier.rental.domain.Category;
import com.ahmedsghaier.rental.domain.Product;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.util.List;

/**
 * Modal form for creating or editing a {@link Product}.
 *
 * <p>The category list is supplied by the caller so the dialog stays decoupled from the
 * service layer. Invalid price input yields an empty result, letting the caller keep the
 * dialog logic simple.</p>
 */
public class ProductDialog extends Dialog<Product> {

    public ProductDialog(Product existing, List<Category> categories) {
        boolean editing = existing != null && !existing.isNew();
        Product product = existing != null ? existing : new Product();

        setTitle(editing ? "Produkt bearbeiten" : "Neues Produkt");
        setHeaderText(editing ? "Produktdaten bearbeiten" : "Neues Produkt anlegen");
        Dialogs.applyTheme(getDialogPane());
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField label = new TextField(product.getLabel() == null ? "" : product.getLabel());
        label.setPromptText("Bezeichnung");
        label.setPrefWidth(260);

        TextField price = new TextField(
                product.getDailyPrice() == null ? "" : product.getDailyPrice().toPlainString());
        price.setPromptText("z. B. 12.50");

        ComboBox<Category> category = new ComboBox<>();
        category.getItems().addAll(categories);
        category.setPrefWidth(260);
        selectCurrentCategory(category, product);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16, 4, 4, 4));
        grid.add(new Label("Bezeichnung"), 0, 0);
        grid.add(label, 1, 0);
        grid.add(new Label("Preis / Tag"), 0, 1);
        grid.add(price, 1, 1);
        grid.add(new Label("Kategorie"), 0, 2);
        grid.add(category, 1, 2);
        getDialogPane().setContent(grid);

        setResultConverter(button -> {
            if (button != ButtonType.OK) {
                return null;
            }
            product.setLabel(label.getText().trim());
            product.setDailyPrice(parsePrice(price.getText()));
            product.setCategory(category.getValue());
            return product;
        });
    }

    private void selectCurrentCategory(ComboBox<Category> box, Product product) {
        if (product.getCategory() != null) {
            box.getItems().stream()
                    .filter(c -> c.getId() == product.getCategory().getId())
                    .findFirst()
                    .ifPresent(box::setValue);
        }
        if (box.getValue() == null && !box.getItems().isEmpty()) {
            box.setValue(box.getItems().get(0));
        }
    }

    /** Parses user price input leniently, accepting both {@code .} and {@code ,} decimals. */
    private BigDecimal parsePrice(String text) {
        if (text == null || text.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(text.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
