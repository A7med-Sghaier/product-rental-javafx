package gui;

import java.sql.SQLException;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;

public class MainContainer {

	final StackPane mainContainer;
	
	/**
	 * Constructor for the GUI MainContainer
	 */
	public MainContainer()
	{
		this.mainContainer = new StackPane();
		this.initMainContainer();
	}
	
	/**
	 * Initialize the GUI for the main container 
	 */
	public void initMainContainer() 
	{
		this.mainContainer.getStyleClass().add("main-container");
	};
	
	/**
	 * Initialize the Pane for the main container 
	 */
	public StackPane getMainContainer()
	{
		return this.mainContainer;
	}
	
	/**
	 * Add a given to the main container
	 */
	public void setContent(Group group)
	{
		this.mainContainer.getChildren().clear();
		this.mainContainer.getChildren().addAll(group);
	}
	
}
