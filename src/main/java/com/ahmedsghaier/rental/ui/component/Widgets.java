package com.ahmedsghaier.rental.ui.component;

import com.ahmedsghaier.rental.ui.Styles;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Factory helpers for the small, repeated pieces of the UI (styled buttons, KPI cards,
 * headings) and for locale-aware value formatting.
 *
 * <p>Keeping these in one place keeps the view classes focused on layout and behaviour.</p>
 */
public final class Widgets {

    private static final Locale GERMANY = Locale.GERMANY;
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(GERMANY);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private Widgets() {
    }

    /** @return a filled, accent-coloured primary action button. */
    public static Button primaryButton(String text) {
        return styledButton(text, Styles.BUTTON_PRIMARY);
    }

    /** @return an outlined secondary button. */
    public static Button secondaryButton(String text) {
        return styledButton(text, Styles.BUTTON_SECONDARY);
    }

    /** @return a borderless accent "link" button. */
    public static Button ghostButton(String text) {
        return styledButton(text, Styles.BUTTON_GHOST);
    }

    /** @return an outlined destructive-action button. */
    public static Button dangerButton(String text) {
        return styledButton(text, Styles.BUTTON_DANGER);
    }

    private static Button styledButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        return button;
    }

    /**
     * Builds a KPI (key performance indicator) card with a label and a big value.
     *
     * @param label        the caption
     * @param value        the highlighted value text
     * @param accentClass  optional value colour style class ({@code null} for default)
     */
    public static VBox kpiCard(String label, String value, String accentClass) {
        Label caption = new Label(label.toUpperCase(GERMANY));
        caption.getStyleClass().add(Styles.KPI_LABEL);
        Label figure = new Label(value);
        figure.getStyleClass().add(Styles.KPI_VALUE);

        VBox card = new VBox(caption, figure);
        card.getStyleClass().add(Styles.KPI_CARD);
        if (accentClass != null) {
            card.getStyleClass().add(accentClass);
        }
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    /** @return a bold page heading with a muted subtitle beneath it. */
    public static VBox pageHeader(String title, String subtitle) {
        Label heading = new Label(title);
        heading.getStyleClass().add(Styles.PAGE_TITLE);
        Label sub = new Label(subtitle);
        sub.getStyleClass().add(Styles.PAGE_SUBTITLE);
        VBox box = new VBox(2, heading, sub);
        return box;
    }

    /** @return a flexible horizontal spacer that pushes siblings apart. */
    public static Region grow() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /** @return a status "pill" label styled according to availability. */
    public static Label statusPill(boolean available) {
        Label pill = new Label(available ? "Verfügbar" : "Ausgeliehen");
        pill.getStyleClass().addAll(Styles.PILL,
                available ? Styles.PILL_AVAILABLE : Styles.PILL_RENTED);
        return pill;
    }

    /** Formats a monetary amount as German currency, e.g. {@code 12,50 €}. */
    public static String money(BigDecimal amount) {
        BigDecimal value = amount == null ? BigDecimal.ZERO : amount;
        return CURRENCY.format(value.setScale(2, RoundingMode.HALF_UP));
    }

    /** Formats a date as {@code dd.MM.yyyy}, or an empty string for {@code null}. */
    public static String date(LocalDate value) {
        return value == null ? "" : value.format(DATE);
    }

    /** Wraps content in a centred container that fills available space. */
    public static VBox centered(Region content) {
        VBox box = new VBox(content);
        box.setAlignment(Pos.CENTER);
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }
}
