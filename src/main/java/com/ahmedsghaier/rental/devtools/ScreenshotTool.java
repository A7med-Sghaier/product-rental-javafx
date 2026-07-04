package com.ahmedsghaier.rental.devtools;

import com.ahmedsghaier.rental.config.AppContext;
import com.ahmedsghaier.rental.ui.MainView;
import com.ahmedsghaier.rental.ui.Styles;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Development utility that renders each main screen to a PNG for the README, using JavaFX's
 * off-screen {@code snapshot()} API rather than grabbing an OS window — so the output is
 * deterministic and the same regardless of the desktop environment.
 *
 * <p>The database is seeded with demo data first (if empty) so the screenshots contain
 * meaningful content. Images are written to {@code docs/screenshots/}. Run with:</p>
 *
 * <pre>{@code
 * mvn -q javafx:run -DmainClass=com.ahmedsghaier.rental.devtools.ScreenshotTool
 * }</pre>
 */
public class ScreenshotTool extends Application {

    private static final int WIDTH = 1180;
    private static final int HEIGHT = 760;
    private static final Path OUTPUT_DIR = Path.of("docs", "screenshots");

    @Override
    public void start(Stage stage) throws Exception {
        AppContext context = new AppContext();
        SampleDataLoader.seedIfEmpty(context);

        MainView mainView = new MainView(context);
        Region root = mainView.getRoot();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource(Styles.STYLESHEET)).toExternalForm());

        capture(mainView, mainView::showDashboard, "dashboard.png");
        capture(mainView, mainView::showCustomers, "customers.png");
        capture(mainView, mainView::showProducts, "products.png");
        capture(mainView, mainView::showCategories, "categories.png");

        System.out.println("Screenshots written to " + OUTPUT_DIR.toAbsolutePath());
        Platform.exit();
    }

    private void capture(MainView mainView, Runnable navigate, String fileName) throws Exception {
        navigate.run();
        Region root = mainView.getRoot();
        // Force a CSS + layout pass so the snapshot reflects the final rendered state.
        root.applyCss();
        root.layout();

        WritableImage image = root.snapshot(null, null);
        writePng(image, OUTPUT_DIR.resolve(fileName).toFile());
    }

    /** Writes a JavaFX image to a PNG using only {@code java.desktop} (no javafx-swing). */
    private void writePng(WritableImage image, File file) throws Exception {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        PixelReader reader = image.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffered.setRGB(x, y, reader.getArgb(x, y));
            }
        }
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        ImageIO.write(buffered, "png", file);
        System.out.println("  wrote " + file.getPath() + " (" + width + "x" + height + ")");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
