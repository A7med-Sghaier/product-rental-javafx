package com.ahmedsghaier.rental.ui;

import com.ahmedsghaier.rental.config.AppContext;
import com.ahmedsghaier.rental.ui.view.CategoriesView;
import com.ahmedsghaier.rental.ui.view.CustomersView;
import com.ahmedsghaier.rental.ui.view.DashboardView;
import com.ahmedsghaier.rental.ui.view.NewRentalView;
import com.ahmedsghaier.rental.ui.view.ProductsView;
import com.ahmedsghaier.rental.ui.view.ReturnsView;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * The application shell: a fixed sidebar with navigation and a swappable content area.
 *
 * <p>Implements {@link Navigator} so any view can request a screen change. Navigation
 * entries are {@link ToggleButton}s in a single {@link ToggleGroup}, which keeps exactly
 * one item highlighted at a time.</p>
 */
public class MainView implements Navigator {

    private final AppContext context;
    private final BorderPane root = new BorderPane();
    private final StackPane contentArea = new StackPane();

    private final ToggleGroup navGroup = new ToggleGroup();
    private ToggleButton dashboardNav;
    private ToggleButton customersNav;
    private ToggleButton productsNav;
    private ToggleButton categoriesNav;
    private ToggleButton rentalNav;
    private ToggleButton returnsNav;

    public MainView(AppContext context) {
        this.context = context;
        root.getStyleClass().add(Styles.APP_SHELL);
        contentArea.getStyleClass().add(Styles.CONTENT);
        root.setLeft(buildSidebar());
        root.setCenter(contentArea);
        showDashboard();
    }

    /** @return the root node to place in a {@link javafx.scene.Scene}. */
    public Region getRoot() {
        return root;
    }

    private VBox buildSidebar() {
        Label brand = new Label("Produkt-Ausleihe");
        brand.getStyleClass().add(Styles.SIDEBAR_BRAND);
        Label sub = new Label("Verleih-Verwaltung");
        sub.getStyleClass().add(Styles.SIDEBAR_BRAND_SUB);

        dashboardNav = navButton("Übersicht", this::showDashboard);
        customersNav = navButton("Kunden", this::showCustomers);
        productsNav = navButton("Produkte", this::showProducts);
        categoriesNav = navButton("Kategorien", this::showCategories);
        rentalNav = navButton("Ausleihe", this::showNewRental);
        returnsNav = navButton("Rückgabe", this::showReturns);

        VBox sidebar = new VBox(brand, sub,
                dashboardNav, customersNav, productsNav, categoriesNav, rentalNav, returnsNav);
        sidebar.getStyleClass().add(Styles.SIDEBAR);
        return sidebar;
    }

    private ToggleButton navButton(String text, Runnable action) {
        ToggleButton button = new ToggleButton(text);
        button.getStyleClass().add(Styles.NAV_BUTTON);
        button.setToggleGroup(navGroup);
        button.setMaxWidth(Double.MAX_VALUE);
        // Ignore clicks on the already-selected item so it can't be toggled off.
        button.setOnAction(e -> {
            if (button.isSelected()) {
                action.run();
            } else {
                button.setSelected(true);
            }
        });
        return button;
    }

    @Override
    public void showDashboard() {
        select(dashboardNav);
        show(new DashboardView(context, this).getRoot());
    }

    @Override
    public void showCustomers() {
        select(customersNav);
        show(new CustomersView(context).getRoot());
    }

    @Override
    public void showProducts() {
        select(productsNav);
        show(new ProductsView(context).getRoot());
    }

    @Override
    public void showCategories() {
        select(categoriesNav);
        show(new CategoriesView(context).getRoot());
    }

    @Override
    public void showNewRental() {
        select(rentalNav);
        show(new NewRentalView(context, this).getRoot());
    }

    @Override
    public void showReturns() {
        select(returnsNav);
        show(new ReturnsView(context).getRoot());
    }

    @Override
    public void show(Region content) {
        contentArea.getChildren().setAll(content);
    }

    private void select(ToggleButton button) {
        button.setSelected(true);
    }
}
