package main;

import java.sql.SQLException;
import java.util.ArrayList;

import db.DB;
import gui.MainGui;
import javafx.application.Application;
import javafx.stage.Stage;
import product.Status;

/**
 * Class Leihaus
 */
public class Leihaus extends Application{

	@Override public void start(Stage stage) throws SQLException, Exception {
		MainGui mainGui = new MainGui();
		mainGui.initMainGui(stage);
	}
	
	public static DB db = new DB(); // Initialize the DB 
	public static ArrayList<Status> statusItems = new ArrayList<Status>();

	/**
	 * Main Function for the Application
	 * @param args
	 * @throws SQLException
	 * @throws Exception
	 */
	public static void main(String[] args) throws SQLException, Exception {
		db.initDB();
		statusItems.add(new Status(-1, "Filter Ausblenden"));
		statusItems.add(new Status(0, "Verfügbar"));
		statusItems.add(new Status(1, "ausgeliehen"));
		
		launch(args);
	}

}
