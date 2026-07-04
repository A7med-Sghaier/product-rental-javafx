package com.ahmedsghaier.rental.ui;

import com.ahmedsghaier.rental.config.AppContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * JavaFX entry point for the Produkt-Ausleihe application.
 *
 * <p>Builds the {@link AppContext} (which initialises the database and wires the services),
 * mounts the {@link MainView} shell into a scene and applies the design-system stylesheet.</p>
 */
public class RentalApplication extends Application {

    @Override
    public void start(Stage stage) {
        AppContext context = new AppContext();
        MainView mainView = new MainView(context);

        Scene scene = new Scene(mainView.getRoot(), 1120, 720);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource(Styles.STYLESHEET)).toExternalForm());

        stage.setTitle("Produkt-Ausleihe");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
