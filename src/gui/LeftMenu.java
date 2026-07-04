package gui;

import java.sql.SQLException;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import main.Leihaus;


public class LeftMenu {
	
	final Button overViewBt;
	final Button clientsBt;
	final Button productsBt;
	final Button categoriestBt;
	final Button rentsBt;
	final Button backsBt;

	/**
	 * Constructor for the GUI LeftMenu
	 * @param relatedContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public LeftMenu(MainContainer relatedContainer){
		this.overViewBt = new Button("Übersicht");
		this.clientsBt = new Button("Kunden");
		this.productsBt = new Button("Produkte");
		this.categoriestBt = new Button("Kategorien");
		this.rentsBt = new Button("Ausleihe");
		this.backsBt = new Button("Rückgabe");
		
		this.initButtonHandles(relatedContainer);
		this.initMenu();
	};
	
	/**
	 * Initialize the Left Menu style items
	 */
	public void initMenu()
	{
		overViewBt.getStyleClass().addAll("btn", "spacing-15");
		clientsBt.getStyleClass().addAll("btn", "spacing-15");
		productsBt.getStyleClass().addAll("btn", "spacing-15");
		categoriestBt.getStyleClass().addAll("btn", "spacing-15");
		rentsBt.getStyleClass().addAll("btn", "spacing-15");
		backsBt.getStyleClass().addAll("btn", "spacing-15");
	}
	
	/**
	 * Initialize the left menu Buttons 
	 * @param relatedContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public void initButtonHandles(MainContainer relatedContainer)
	{
		this.initOverViewBtEventHandle(relatedContainer);
		this.initClientsBtEventHandle(relatedContainer);
		this.initProduktsBtEventHandle(relatedContainer);
		this.initCategoriestBtEventHandle(relatedContainer);
		this.initRentsBtEventHandle(relatedContainer);
		this.initBacksBtEventHandle(relatedContainer);
	}
	
	/**
	 * Initial the EventHandler for the overView Button
	 * @param relatedContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public void initOverViewBtEventHandle(MainContainer relatedContainer)
	{
		this.overViewBt.setOnAction(action -> {
			OverView overView;
			try {
				overView = new OverView(relatedContainer);
				relatedContainer.setContent(overView.getView());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Initial the EventHandler for the Clients Button
	 * @param relatedContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public void initClientsBtEventHandle(MainContainer relatedContainer)
	{
		this.clientsBt.setOnAction(action -> {
			ClientOverView clientOverView;
			try {
				clientOverView = new ClientOverView(relatedContainer);
				relatedContainer.setContent(clientOverView.getView());
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Initial the EventHandler for the products Button
	 * @param relatedContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public void initProduktsBtEventHandle(MainContainer relatedContainer)
	{
		this.productsBt.setOnAction(action -> {
			ProductOverView productOverview;
			try {
				productOverview = new ProductOverView(relatedContainer);
				relatedContainer.setContent(productOverview.getView());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
	}
	
	/**
	 * Initial the EventHandler for the categories Button
	 * @param relatedContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public void initCategoriestBtEventHandle(MainContainer relatedContainer)
	{
		this.categoriestBt.setOnAction(action -> {
			CategoryOverView categoriesOverview;
			try {
				categoriesOverview = new CategoryOverView(relatedContainer);
				relatedContainer.setContent(categoriesOverview.getView());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
	}
	
	/**
	 * Initial the EventHandler for the rents Button
	 * @param relatedContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public void initRentsBtEventHandle(MainContainer relatedContainer)
	{
		this.rentsBt.setOnAction(action -> {
			RentOverview rentOverview;
			try {
				rentOverview = new RentOverview(relatedContainer);
				relatedContainer.setContent(rentOverview.getView());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
	}
	
	/**
	 * Initial the EventHandler for the returns Button
	 * @param relatedContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public void initBacksBtEventHandle(MainContainer relatedContainer)
	{
		this.backsBt.setOnAction(action ->{
			ReturnOverView returnOverview;
			try {
				returnOverview = new ReturnOverView(relatedContainer);
				relatedContainer.setContent(returnOverview.getView());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Initialize the final GUI for the left menu
	 * @return : Group of elements
	 */
	public VBox getLeftMenu() {
		VBox vbox = new VBox(this.overViewBt, this.clientsBt, this.productsBt, this.categoriestBt, this.rentsBt, this.backsBt);
		vbox.getStyleClass().add("left-menu");
		return vbox;
	}
	
}
