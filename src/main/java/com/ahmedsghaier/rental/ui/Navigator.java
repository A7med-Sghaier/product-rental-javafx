package com.ahmedsghaier.rental.ui;

import javafx.scene.layout.Region;

/**
 * Abstraction the views use to move between screens without knowing about the
 * {@link MainView} shell that hosts them.
 *
 * <p>The primary destinations map to sidebar entries; {@link #show(Region)} allows a view
 * to display an ad-hoc screen (such as a generated invoice) that has no sidebar entry.</p>
 */
public interface Navigator {

    void showDashboard();

    void showCustomers();

    void showProducts();

    void showCategories();

    void showNewRental();

    void showReturns();

    /** Displays an arbitrary screen in the content area without changing the selected nav item. */
    void show(Region content);
}
