package com.ahmedsghaier.rental.ui.dialog;

import com.ahmedsghaier.rental.ui.Styles;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.util.Objects;

/**
 * Thin helpers for the recurring confirmation, warning and error dialogs, styled with the
 * application stylesheet so they match the rest of the UI.
 */
public final class Dialogs {

    private Dialogs() {
    }

    /**
     * Shows a yes/no confirmation dialog.
     *
     * @return {@code true} if the user confirmed with OK
     */
    public static boolean confirm(String title, String message) {
        Alert alert = build(Alert.AlertType.CONFIRMATION, title, message);
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    /** Shows a non-blocking informational warning. */
    public static void warn(String message) {
        build(Alert.AlertType.WARNING, "Hinweis", message).showAndWait();
    }

    /** Shows an error dialog, typically for a failed operation. */
    public static void error(String message) {
        build(Alert.AlertType.ERROR, "Fehler", message).showAndWait();
    }

    private static Alert build(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        applyTheme(alert.getDialogPane());
        return alert;
    }

    /** Applies the application stylesheet to a dialog pane. */
    public static void applyTheme(DialogPane pane) {
        pane.getStylesheets().add(
                Objects.requireNonNull(Dialogs.class.getResource(Styles.STYLESHEET)).toExternalForm());
    }
}
