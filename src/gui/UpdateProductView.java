package gui;

import java.sql.SQLException;

import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.Leihaus;
import product.Category;
import product.ProductDetails;

public class UpdateProductView {

	Label title;
	VBox form;
	HBox footer;
	ProductDetails product;
	
	TextField productLabel;
	TextField preis;
	ChoiceBox<Category> category;
	
	/**
	 * Constructor for the GUI UpdateProductView
	 * @param mainContainer : is the main Panel which contain all elements like Title, Filter, TableView and Filter
	 */
	public UpdateProductView(MainContainer mainContainer) throws SQLException, Exception{
		this.product = new ProductDetails();
		this.initView(mainContainer);
	}
	
	/**
	 * Constructor for the GUI UpdateProductView
	 * @param product : product Object
	 * @param mainContainer : is the main Panel which contain all elements like Title, Filter, TableView and Filter
	 */
	public UpdateProductView(ProductDetails product ,MainContainer mainContainer) throws SQLException, Exception {
		this.product = product;
		this.initView(mainContainer);
	}
	
	/**
	 * Initialize the GUI for a product View
	 * @param mainContainer: is the main Panel which contain all elements like Title, Filter, TableView and Filter
	 */
	public void initView(MainContainer mainContainer) throws SQLException, Exception
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
		this.title = new Label("Neues Produkt");
		this.title.getStyleClass().add("head-title");
	}
	
	/**
	 * Build the Form GUI for a product
	 */
	public void buildForm() throws SQLException, Exception
	{
		this.productLabel = new TextField();
		this.productLabel.setPromptText("Produktname");
		this.productLabel.setText(this.product.getProductname());
		this.productLabel.getStyleClass().addAll("input", "spacing-5");
		
		this.preis = new TextField();
		this.preis.setPromptText("Preis");
		this.preis.setText(this.product.getPreis().toString());
		this.preis.getStyleClass().addAll("input", "spacing-5");
		
		this.category = new ChoiceBox<Category>();
		this.category.getItems().addAll(Leihaus.db.getCategories());
		this.category.getSelectionModel().select(0);
		if(this.product.getCategory() != null) {
			this.category.getItems().stream()
				.filter(category -> category.getId() == this.product.getCategory().getId())
				.findFirst()
				.ifPresent(category -> this.category.getSelectionModel().select(category));
		}
		this.category.getStyleClass().addAll("select-option", "spacing-5");
		
		this.form = new VBox(this.productLabel, this.preis, this.category);
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
				if(!this.isValidProductForm()) {
					return;
				}
				if(this.product.getProductId() == 0) { // add product
					Leihaus.db.addProduct(this.saveProduct());
				} else { // update product
					Leihaus.db.updateProduct(this.saveProduct());
				}
				// change the GUI view
				mainContainer.setContent(new ProductOverView(mainContainer).getView());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		/* Initialize the EventHandler for cancel Button*/
		cancel.setOnAction(action -> {
			try {
				mainContainer.setContent(new ProductOverView(mainContainer).getView());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		this.footer = new HBox(save, cancel);
		this.footer.getStyleClass().addAll("table-view-footer", "align-center");
	}	

	private boolean isBlank(TextField field)
	{
		return field.getText() == null || field.getText().trim().isEmpty();
	}

	private void showValidationWarning(String message)
	{
		Alert warningAlert = new Alert(AlertType.WARNING);
		warningAlert.setGraphic(null);
		warningAlert.setHeaderText(message);
		warningAlert.showAndWait();
	}

	private boolean isValidProductForm()
	{
		if(this.isBlank(this.productLabel)) {
			this.showValidationWarning("Bitte geben Sie einen Produktnamen ein.");
			return false;
		}
		if(this.isBlank(this.preis)) {
			this.showValidationWarning("Bitte geben Sie einen Preis ein.");
			return false;
		}
		try {
			float parsedPrice = Float.parseFloat(this.preis.getText().trim());
			if(parsedPrice <= 0) {
				this.showValidationWarning("Der Preis muss größer als 0 sein.");
				return false;
			}
		} catch(NumberFormatException e) {
			this.showValidationWarning("Bitte geben Sie einen gültigen Preis ein.");
			return false;
		}
		if(this.category.getSelectionModel().getSelectedItem() == null) {
			this.showValidationWarning("Bitte wählen Sie eine Kategorie aus.");
			return false;
		}
		return true;
	}
	
	/**
	 * Prepare the save function
	 * @return
	 */
	public ProductDetails saveProduct()
	{
		this.product.setProductname(this.productLabel.getText().trim());
		this.product.setPreis(Float.parseFloat(this.preis.getText().trim()));
		this.product.setCategory(this.category.getSelectionModel().getSelectedItem());
		return this.product;
	}
	
	/**
	 * Initialize the final GUI for the UpdateProductView
	 * @return : Group of elements
	 */
	public Group getView()
	{
		VBox vbox = new VBox(this.title, this.form, this.footer);
		vbox.getStyleClass().add("head-title-container");
		return new Group(vbox);
	}
	
	
}
