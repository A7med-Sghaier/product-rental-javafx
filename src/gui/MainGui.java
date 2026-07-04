package gui;

import java.sql.SQLException;

import db.DB;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainGui{
	
	/**
	 * Initialize the GUI Window 
	 */
	public void initMainGui(Stage stage) throws SQLException, Exception {

		final MainContainer mainContainer = new MainContainer(); // right main container
		final LeftMenu leftMenu = new LeftMenu(mainContainer); // left menu container
		
		OverView overView = new OverView(mainContainer); // initialize the OverView as a default View
		
		mainContainer.setContent(overView.getView());
		
		/* Set the Left menu */
		final HBox parentContainer = new HBox(leftMenu.getLeftMenu(), mainContainer.getMainContainer());
		
		parentContainer.getStyleClass().add("parent-container");
		
		/* Initialize the Scene*/
		Scene scene = new Scene(parentContainer, 900, 600);
		scene.getStylesheets().clear();
		scene.getStylesheets().add("/assets/styles/style.css"); // Initilize the global style
		
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}
}
