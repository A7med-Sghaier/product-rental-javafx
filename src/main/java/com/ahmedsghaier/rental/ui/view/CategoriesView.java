package com.ahmedsghaier.rental.ui.view;

import com.ahmedsghaier.rental.config.AppContext;
import com.ahmedsghaier.rental.domain.Category;
import com.ahmedsghaier.rental.domain.exception.ValidationException;
import com.ahmedsghaier.rental.service.CategoryService;
import com.ahmedsghaier.rental.ui.Styles;
import com.ahmedsghaier.rental.ui.View;
import com.ahmedsghaier.rental.ui.component.Widgets;
import com.ahmedsghaier.rental.ui.dialog.CategoryDialog;
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

import java.util.Locale;

/**
 * Screen for managing product categories.
 */
public class CategoriesView implements View {

    private final CategoryService categoryService;
    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private final FilteredList<Category> filtered = new FilteredList<>(categories, c -> true);
    private final TableView<Category> table = new TableView<>(filtered);

    public CategoriesView(AppContext context) {
        this.categoryService = context.categoryService();
    }

    @Override
    public Region getRoot() {
        VBox header = Widgets.pageHeader("Kategorien", "Produktkategorien verwalten");

        TextField search = new TextField();
        search.setPromptText("Kategorie suchen …");
        search.getStyleClass().add(Styles.SEARCH_FIELD);
        search.textProperty().addListener((obs, old, value) ->
                filtered.setPredicate(c -> matches(c, value)));

        Button add = Widgets.primaryButton("＋ Neue Kategorie");
        add.setOnAction(e -> openForm(null));

        Button edit = Widgets.secondaryButton("Bearbeiten");
        edit.setOnAction(e -> openForm(table.getSelectionModel().getSelectedItem()));
        edit.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        Button delete = Widgets.dangerButton("Löschen");
        delete.setOnAction(e -> delete(table.getSelectionModel().getSelectedItem()));
        delete.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        HBox toolbar = new HBox(10, search, Widgets.grow(), edit, delete, add);

        TableColumn<Category, String> labelCol = new TableColumn<>("Bezeichnung");
        labelCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLabel()));
        table.getColumns().setAll(java.util.List.of(labelCol));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(placeholder("Noch keine Kategorien vorhanden."));
        reload();

        VBox root = new VBox(20, header, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    private void reload() {
        categories.setAll(categoryService.findAll());
    }

    private void openForm(Category category) {
        new CategoryDialog(category).showAndWait().ifPresent(edited -> {
            try {
                categoryService.save(edited);
                reload();
            } catch (ValidationException ex) {
                Dialogs.warn(ex.getMessage());
            }
        });
    }

    private void delete(Category category) {
        if (category == null) {
            return;
        }
        if (Dialogs.confirm("Kategorie löschen", "Möchten Sie die Kategorie \""
                + category.getLabel() + "\" wirklich löschen? Zugehörige Produkte werden ebenfalls entfernt.")) {
            categoryService.delete(category);
            reload();
        }
    }

    private boolean matches(Category category, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        return category.getLabel() != null && category.getLabel()
                .toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
    }

    private Label placeholder(String text) {
        Label label = new Label(text);
        label.getStyleClass().add(Styles.MUTED);
        return label;
    }
}
