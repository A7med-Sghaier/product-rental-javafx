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
import product.Category;
import product.ProductDetails;
import product.Status;

public class InvoiceView {


	MainContainer mainContainer;
	
	Label title;
	public GridPane clientContainer ;
	public GridPane productsContainer ;
	public GridPane totalPreisContainer;
	
	Client client;
	public Button printBt;
	
	Label clientLabel = new Label();
	Float totalPreis;
	Label totalPreisLabel = new Label();
	
	HBox footer;
	
	public TableView<ProductDetails> productsTableView = new TableView<ProductDetails>();
	public ObservableList<ProductDetails> productList = FXCollections.observableArrayList();
	public FilteredList<ProductDetails> filteredProductList = new FilteredList<>(this.productList, productdetail -> true);
	
	/**
	 * Constructor for the GUI InvoiceView
	 */
	public InvoiceView()
	{}
	
	/**
	 * Constructor for the GUI InvoiceView
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 * @param client : client Object
	 * @param productList : list of products
	 * @throws SQLException : needed to throw errors from SQL
	 * @throws Exception : needed to throw global errors
	 */
	public InvoiceView(Client client, ObservableList<ProductDetails> productList, MainContainer mainContainer)
	{
		this.mainContainer = mainContainer;
		this.client = client;
		this.productList.addAll(productList.sorted());
		this.buildTitle();
		this.buildClientContainer();
		this.buildProductsContainer();
		this.buildFooter();
		this.computeTotalPrice();
	}
	
	/**
	 * Build the title for GUI
	 */
	public void buildTitle()
	{
		this.title = new Label("Verleihbestätigung/Quittung");
		this.title.getStyleClass().add("head-title");
	}
	
	/**
	 * Build the client label container
	 */
	public void buildClientContainer()
	{
		this.buildClientLabel();
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
		
		clientContainer.getColumnConstraints().addAll(col1, col2, col3);
		clientContainer.setHgap(5);
		clientContainer.setVgap(5);
		
		clientContainer.add(title, 0, 0, 1, 1);
		clientContainer.add(separator, 1, 0, 1, 1);
		clientContainer.add(this.clientLabel, 2, 0, 2, 1);
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
			
	}
	
	public void buildClientLabel()
	{
		this.clientLabel.setText(this.client.getFirstname() + " " + this.client.getLastname());
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
		this.productsTableView.getStyleClass().add("client-over-view");
	}

	/**
	 * Build the total Price container
	 */
	public void buildTotalPriceContainer()
	{
		this.totalPreisContainer = new GridPane( );
		Label totalLabel = new Label("Total");
		totalLabel.getStyleClass().addAll("size-18","text-white");
		Label separator = new Label(":");
		separator.getStyleClass().addAll("size-18","text-white");
		Label prefix = new Label(" €");
		prefix.getStyleClass().addAll("size-18","text-white");
		this.totalPreisLabel.setText(this.totalPreis.toString());
		this.totalPreisLabel.getStyleClass().addAll("size-18","text-white");
		
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(80);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(5);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(15);
		ColumnConstraints col4 = new ColumnConstraints();
		col4.setPercentWidth(5);
		
		this.totalPreisContainer.getColumnConstraints().addAll(col1, col2, col3);
		this.totalPreisContainer.setHgap(5);
		this.totalPreisContainer.setVgap(5);

		GridPane.setHalignment(totalLabel, HPos.RIGHT);
		this.totalPreisContainer.add(totalLabel, 0, 0, 1, 1);
		this.totalPreisContainer.add(separator, 1, 0, 1, 1);
		this.totalPreisContainer.add(this.totalPreisLabel, 2, 0, 1, 1);
		this.totalPreisContainer.add(prefix, 3, 0, 1, 1);

	}
	
	/**
	 * Compute the Total price of an invoice
	 */
	public void computeTotalPrice()
	{
		this.totalPreis = (float) 0;
		this.productList.forEach(item -> {
			this.totalPreis += item.calculateTotal();
		});
		this.buildTotalPriceContainer();
	}
	
	/**
	 * Build the footer that contains action Buttons like add, edit and remove
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public void buildFooter()
	{
		this.printBt = new Button("Drücken");
		this.printBt.getStyleClass().addAll("btn", "spacing-15");
		
		//Helpers.createInvoice(invoiceBt, this);
		
		this.footer = new HBox(this.printBt);
		this.footer.getStyleClass().addAll("table-view-footer", "align-right");
	}
	
	/**
	 * Initialize the final GUI for the invoice over view
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
		GridPane.setHalignment(this.totalPreisContainer, HPos.RIGHT);
		gPane.add(this.totalPreisContainer, 0, 3);
		GridPane.setHalignment(this.footer, HPos.CENTER);
		gPane.add(this.footer, 0, 4);

		return new Group(gPane);
	}
}
