package com.ahmedsghaier.rental.ui.dialog;

import com.ahmedsghaier.rental.domain.Category;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Modal form for creating or editing a {@link Category}.
 */
public class CategoryDialog extends Dialog<Category> {

    public CategoryDialog(Category existing) {
        boolean editing = existing != null && !existing.isNew();
        Category category = existing != null ? existing : new Category();

        setTitle(editing ? "Kategorie bearbeiten" : "Neue Kategorie");
        setHeaderText(editing ? "Kategorie bearbeiten" : "Neue Kategorie anlegen");
        Dialogs.applyTheme(getDialogPane());
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField label = new TextField(category.getLabel() == null ? "" : category.getLabel());
        label.setPromptText("Bezeichnung");
        label.setPrefWidth(260);

        VBox content = new VBox(8, new Label("Bezeichnung"), label);
        content.setPadding(new Insets(16, 4, 4, 4));
        getDialogPane().setContent(content);

        setResultConverter(button -> {
            if (button != ButtonType.OK) {
                return null;
            }
            category.setLabel(label.getText().trim());
            return category;
        });
    }
}
