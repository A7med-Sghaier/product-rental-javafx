package gui;


import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;

import javafx.scene.Group;
import javafx.scene.layout.VBox;
import main.Leihaus;
import javafx.scene.layout.HBox;

import product.Category;

public class UpdateCategoryView {
	Label title;
	VBox form;
	HBox footer;
	Category category;
	TextField label;
	
	/**
	 * Constructor for the GUI UpdateCategoryView
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public UpdateCategoryView(MainContainer mainContainer) {
		this.category = new Category();
		this.initView(mainContainer);
	}
	
	/**
	 * Constructor for the GUI UpdateCategoryView
	 * @param category : category Object
	 * @param mainContainer : is the main Panel which contain all elements like Title, Filter, TableView and Filter
	 */
	public UpdateCategoryView(Category category ,MainContainer mainContainer) {
		this.category = category;
		this.initView(mainContainer);
	}
	
	/**
	 * Initialize the GUI for a category View
	 * @param mainContainer: is the main Panel which contain all elements like Title, Filter, TableView and Filter
	 */
	public void initView(MainContainer mainContainer)
	{
		this.buildTitle();
		this.buildForm();
		this.buildFooter(mainContainer);
	}
	
	/**
	 * Build the title for GUI
	 */
	public void buildTitle()
	{
		this.title = new Label("Neue Kategorie");
		this.title.getStyleClass().add("head-title");
	}
	
	/**
	 * Build the Form GUI for a category
	 */
	public void buildForm()
	{
		this.label = new TextField();
		this.label.setPromptText("Bezeichnung");
		this.label.setText(this.category.getLabel());
		this.label.getStyleClass().addAll("input", "spacing-5");
		
		this.form = new VBox(this.label);
		this.form.getStyleClass().addAll("input-form");
	}

	/**
	 * Build the footer that contains action Buttons like add, edit and remove
	 * @param mainContainer : is the main Panel which contain all elements like Title, Filter, TableView and Filter
	 */
	public void buildFooter(MainContainer mainContainer)
	{
		Button save = new Button("Speichern");
		Button cancel = new Button("Abbrechen");
		
		save.getStyleClass().addAll("btn", "spacing-15");
		cancel.getStyleClass().addAll("btn", "spacing-15");
		
		/* Initialize the EventHandler for save Button*/
		save.setOnAction(action -> {
			try {
				if(this.category.getId() == 0) { // add category
					Leihaus.db.addCategory(this.saveCategory());
				} else { // update category
					Leihaus.db.updateCategory(this.saveCategory());
				}
				// change the GUI view
				mainContainer.setContent(new CategoryOverView(mainContainer).getView());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		/* Initialize the EventHandler for cancel Button*/
		cancel.setOnAction(action -> {
			try {
				mainContainer.setContent(new CategoryOverView(mainContainer).getView());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		this.footer = new HBox(save, cancel);
		this.footer.getStyleClass().addAll("table-view-footer", "align-center");
	}
	
	/**
	 * Prepare the save function
	 * @return
	 */
	public Category saveCategory()
	{
		this.category.setLabel(this.label.getText());
		
		return this.category;
	}
	
	/**
	 * Initialize the final GUI for the UpdateCategoryView
	 * @return : Group of elements
	 */
	public Group getView()
	{
		VBox vbox = new VBox(this.title, this.form, this.footer);
		vbox.getStyleClass().add("head-title-container");
		return new Group(vbox);
	}
	
}
