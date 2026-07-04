package gui;


import java.sql.SQLException;

import buttonHandlers.Helpers;
import client.Client;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import main.Leihaus;
import product.Category;
import product.ProductDetails;
import product.Status;

public class ReturnOverView {

	MainContainer mainContainer;
	Label title;
	TextField searchFilter;
	public GridPane clientContainer ;
	public GridPane productsContainer ;
	Button selectClient;
	public Button returnProductBt;
	HBox footer;
	
	Label clientLabel = new Label();
	
	public TableView<Client> clientTableView = new TableView<Client>();
	public TableView<ProductDetails> productsTableView = new TableView<ProductDetails>();
	
	public ObservableList<Client> clientList = FXCollections.observableArrayList();
	private FilteredList<Client> filteredClientList = new FilteredList<>(this.clientList, client -> true);
	
	public ObservableList<ProductDetails> productList = FXCollections.observableArrayList();
	public FilteredList<ProductDetails> filteredProductList = new FilteredList<>(this.productList, productdetail -> true);
	
	/**
	 * Constructor for the GUI ReturnOverView
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public ReturnOverView(MainContainer mainContainer) 
	{
		this.mainContainer = mainContainer;
		this.selectClient = new Button("Kunde auswählen");
		this.selectClient.getStyleClass().addAll("btn", "spacing-15");
		this.initSelectButtonsAction();
		this.buildTitle();
		this.buildClientContainer();
		this.buildProductsContainer();
		this.buildFooter();
	}
	
	/**
	 * Initialize the Selectoptions Buttons
	 */
	public void initSelectButtonsAction()
	{
		Helpers.selectClientForReturn(this.selectClient, this);
	}

	/**
	 * Build the title for GUI
	 */
	public void buildTitle()
	{
		this.title = new Label("Produkt(e) ausleihen");
		this.title.getStyleClass().add("head-title");
	}
	
	/**
	 * Build the client container
	 */
	public void buildClientContainer()
	{
		Label title = new Label("Kunde");
		title.getStyleClass().addAll("filter-label");

		Label separator = new Label(":");
		separator.getStyleClass().addAll("filter-label");
		
		this.clientContainer = new GridPane( );
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(25);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(5);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(35);
		ColumnConstraints col4 = new ColumnConstraints();
		col4.setPercentWidth(35);
		
		clientContainer.getColumnConstraints().addAll(col1, col2, col3, col4);
		clientContainer.setHgap(5);
		clientContainer.setVgap(5);
		
		clientContainer.add(title, 0, 0, 1, 1);
		clientContainer.add(separator, 1, 0, 1, 1);
		if(this.clientList.isEmpty()) {
			clientContainer.add(this.selectClient, 2, 0, 2, 1);
		} else {
			clientContainer.add(this.clientLabel, 2, 0, 1, 1);
			clientContainer.add(this.selectClient, 3, 0, 1, 1);
		}
	}
	
	/**
	 * Build the products TableView container
	 */
	public void buildProductsContainer()
	{
		this.buildProductTableView();
		Label title = new Label("Produkte");
		title.getStyleClass().addAll("filter-label");

		Label separator = new Label(":");
		separator.getStyleClass().addAll("filter-label");
		
		this.productsContainer = new GridPane( );
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(25);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(5);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(70);
		
		productsContainer.getColumnConstraints().addAll(col1, col2, col3);
		productsContainer.setHgap(5);
		productsContainer.setVgap(5);
		
		productsContainer.add(title, 0, 0, 1, 1);
		productsContainer.add(separator, 1, 0, 1, 1);
		productsContainer.add(this.productsTableView, 0, 1, 3, 1);
		this.productsContainer.visibleProperty().bind(Bindings.size(this.clientList).isNotEqualTo(0));
			
	}
	
	/**
	 * Build the client label
	 */
	public void buildClientLabel()
	{
		this.clientLabel.setText(this.clientList.get(0).getFirstname() + " " + this.clientList.get(0).getLastname());
		this.clientLabel.getStyleClass().addAll("size-18","text-white");
	}
	
	/**
	 * Initialize the products list TableView
	 */
	public void buildProductTableView()
	{
		TableColumn productCol = new TableColumn("Produkt");
	    TableColumn preisCol = new TableColumn("€/Tag");
	    TableColumn daysCol = new TableColumn("Tage");
	    TableColumn dateFromCol = new TableColumn("Leihdatum");
	    TableColumn dateToCol = new TableColumn("Rückgabedatum");
	    
	    productCol.setCellValueFactory(new PropertyValueFactory<ProductDetails, String>("productname"));
	    preisCol.setCellValueFactory(new PropertyValueFactory<ProductDetails, Float>("preis"));
	    daysCol.setCellValueFactory(new PropertyValueFactory<ProductDetails, String>("periode"));
	    dateFromCol.setCellValueFactory(new PropertyValueFactory<ProductDetails, String>("datefrom"));
	    dateToCol.setCellValueFactory(new PropertyValueFactory<ProductDetails, String>("dateto"));
	    
	    this.productsTableView.setItems(this.filteredProductList);
		this.productsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		this.productsTableView.setMaxHeight(250);
		this.productsTableView.getColumns().addAll(productCol, preisCol, daysCol, dateFromCol, dateToCol);
	}
	
	/**
	 * Rebuild the current GUI View
	 */
	public void rebuildView()
	{
		this.mainContainer.setContent(this.getView());
	}
	
	/**
	 * Build the footer that contains action Buttons like add, edit and remove
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public void buildFooter()
	{
		this.returnProductBt = new Button("Rückgeben");
		this.returnProductBt.getStyleClass().addAll("btn", "spacing-15");
		this.returnProductBt.disableProperty().bind(Bindings.isEmpty(this.productsTableView.selectionModelProperty().get().getSelectedCells()));
		
		Helpers.returnProductBt(returnProductBt, this);
		
		this.footer = new HBox(this.returnProductBt);
		this.footer.getStyleClass().addAll("table-view-footer", "align-right");
		this.footer.visibleProperty().bind(Bindings.size(this.clientList).isNotEqualTo(0));
	}
	
	/**
	 * Initialize the selected client 
	 * @param clientId : client Id
	 */
	public void initClient(int clientId)
	{
		try {
			Client client = Leihaus.db.getClientById(clientId);
			this.clientList.clear();
			this.clientList.add(client);
			this.productList.clear();
			this.productList.addAll(Leihaus.db.getRentedProductsByClient(client));
			this.buildClientLabel();
			this.buildClientContainer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Initialize the final GUI for the ReturnsOverView
	 * @return : Group of elements
	 */
	public Group getView()
	{
		GridPane gPane = new GridPane();
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(5);
		RowConstraints row2 = new RowConstraints();
		RowConstraints row3 = new RowConstraints();
		RowConstraints row4 = new RowConstraints();
		RowConstraints row5 = new RowConstraints();
		row5.setPercentHeight(5);
		gPane.getRowConstraints().addAll(row1, row2, row3, row4, row5);
		gPane.setHgap(5);
		gPane.setVgap(5);
		
		GridPane.setHalignment(this.title, HPos.CENTER);
		gPane.add(this.title, 0, 0);
		gPane.add(this.clientContainer, 0, 1);
		gPane.add(this.productsContainer, 0, 2);
		GridPane.setHalignment(this.footer, HPos.CENTER);
		gPane.add(this.footer, 0, 4);

		return new Group(gPane);
	}
}
