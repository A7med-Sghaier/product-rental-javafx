package gui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import buttonHandlers.FilterHelpers;
import buttonHandlers.Helpers;
import client.Client;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.HPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Group;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;
import main.Leihaus;
import product.Category;
import product.ProductDetails;
import product.Rent;
import product.Status;
import javafx.scene.layout.HBox;

public class ProductOverView {

	Label title;
	TextField searchFilter;
	ChoiceBox<Category> categoryFilter;
	ChoiceBox<Status> statusFilter;
	GridPane filterContainer;
	HBox footer;
	
	public DatePicker dateFrom;
	public DatePicker dateTo;
	
	public TableView<ProductDetails> tableView = new TableView<ProductDetails>();
	public ObservableList<Pair<String,String>> multiFilter = FXCollections.observableArrayList();
	public ObservableList<ProductDetails> productList = FXCollections.observableArrayList();
	private FilteredList<ProductDetails> filteredProductList = new FilteredList<>(this.productList, productdetails -> true);

	/**
	 * Constructor for the GUI ProductOverView
	 * @throws SQLException : needed to throw errors from SQL
	 * @throws Exception : needed to throw global errors
	 */
	public ProductOverView() throws SQLException, Exception {
		this.initDates();
		this.initProductList();
		this.buildTitle();
		this.buildFilter();
		this.buildTabView();
	}
	
	/**
	 * Constructor for the GUI ProductOverView
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 * @throws SQLException : needed to throw errors from SQL
	 * @throws Exception : needed to throw global errors
	 */
	public ProductOverView(MainContainer mainContainer) throws SQLException, Exception {
		this.initOverview(mainContainer);
	}
	
	/**
	 * Initialize defaults Dates for DatePicker
	 * set DateFrom to current date
	 * set DateTo to 7 days later of the current date
	 */
	public void initDates()
	{
		this.dateFrom = new DatePicker(LocalDate.now());
		this.dateTo = new DatePicker(LocalDate.now().plus(7, ChronoUnit.DAYS));
		this.dateFrom.setShowWeekNumbers(true);
		this.dateTo.setShowWeekNumbers(true);
		this.disablePastDates();
	}
	
	/**
	 * Restrict the Date in DatePicker
	 * Disable past Date from DatePicker 
	 */
	public void disablePastDates()
	{
		final Callback<DatePicker, DateCell> dayCellFactory = 
	            new Callback<DatePicker, DateCell>() {
	                @Override
	                public DateCell call(final DatePicker datePicker) {
	                    return new DateCell() {
	                        @Override
	                        public void updateItem(LocalDate item, boolean empty) {
	                            super.updateItem(item, empty);
	                            if (item.isBefore(
	                            		dateFrom.getValue())
	                                ) {
	                                    setDisable(true);
	                                    setStyle("-fx-background-color: #ffc0cb;");
	                            }   
	                    }
	                };
	            }
	        };
	        
	        this.dateFrom.setDayCellFactory(dayCellFactory);
	        this.dateTo.setDayCellFactory(dayCellFactory);
	}
	
	/**
	 * Initialize the overview GUI for the list of the current rented Products 
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 * @throws SQLException : needed to throw errors from SQL
	 * @throws Exception : needed to throw global errors
	 */
	public void initOverview(MainContainer mainContainer) throws SQLException, Exception
	{
		this.initProductList();
		this.buildTitle();
		this.buildFilter();
		this.buildTabView();
		this.buildFooter(mainContainer);
	}
	
	/**
	 * Initialize the list of rented products
	 * Get all rented products from DB
	 * @throws SQLException : needed to throw errors from SQL
	 * @throws Exception : needed to throw global errors
	 */
	public void initProductList() throws SQLException, Exception
	{
		productList.clear();
		productList.addAll(Leihaus.db.getProductOverView());
	}
	
	/**
	 * Build the title for GUI
	 */
	public void buildTitle()
	{
		this.title = new Label("Produkte");
		this.title.getStyleClass().add("head-title");
	}
	
	/**
	 * Prepare the filter elements
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void buildFilter() throws SQLException, Exception
	{
		/* initialize the search by product name filter element */
		this.searchFilter = new TextField();
		this.searchFilter.setPromptText("Search...");
		this.searchFilter.getStyleClass().addAll("filter");
		
		/* initialize the categories filter element */
		this.categoryFilter = new ChoiceBox<Category>();
		this.categoryFilter.getItems().add(new Category(-1, "Filter Ausblenden"));
		this.categoryFilter.getItems().addAll(Leihaus.db.getCategories());
		this.categoryFilter.getSelectionModel().select(0);
		this.categoryFilter.getStyleClass().addAll("filter", "spacing-5");
		
		/* initialize the status filter element */
		this.statusFilter = new ChoiceBox<Status>();
		this.statusFilter.getItems().addAll(Leihaus.statusItems);
		this.statusFilter.getSelectionModel().select(0);
		this.statusFilter.getStyleClass().addAll("filter", "spacing-5");
		
		/* add an EventHadler to detect changes of search by product name filter*/
		this.searchFilter.textProperty().addListener((observable, oldVal, newVal) -> {
			this.multiFilter.remove(new Pair<String, String>("product",oldVal));
			this.multiFilter.add(new Pair<String, String>("product",newVal));
		});
		
		/* add an EventHadler to detect changes of categories filter*/
		this.categoryFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
			String oldValue = "-1";
			String newValue = "-1";
			if(oldVal != null) {
				oldValue = ((Integer)oldVal.getId()).toString();
			}
			if(newVal.getId() >= 0) {
				newValue = ((Integer)newVal.getId()).toString();
			}
				this.multiFilter.remove(new Pair<String, String>("category",oldValue));
				this.multiFilter.add(new Pair<String, String>("category",newValue));
		});
		
		/* add an EventHadler to detect changes of status filter*/
		this.statusFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
			String oldValue = "";
			String newValue = "";
			
			if(oldVal != null) {
				oldValue = oldVal.getLabel();
			}
			if(newVal.getId() >= 0) {
				newValue = newVal.getLabel();
			}
			this.multiFilter.remove(new Pair<String, String>("status",oldValue));
			this.multiFilter.add(new Pair<String, String>("status",newValue));
		});
		
		/* add an EventListener to detect changes of the list filer and compute the multifilter */
		this.multiFilter.addListener((Change<? extends Pair<String, String>> change)->{
			while(change.next()) {
				if(change.wasAdded()) {
					this.filteredProductList.setPredicate(product -> {
						return FilterHelpers.multiFilter(product, change.getList());
					});
				}
			}
		});
		
		Label separatorLabel1= new Label(" : ");
		separatorLabel1.getStyleClass().addAll("filter-label");
		
		Label separatorLabel2= new Label(" : ");
		separatorLabel2.getStyleClass().addAll("filter-label");
		
		
		Label categoryFilterLabel= new Label("Produktkategorie");
		categoryFilterLabel.getStyleClass().addAll("filter-label");
		
		Label statusFilterLabel = new Label("Verfübarkeit");
		statusFilterLabel.getStyleClass().addAll("filter-label");

		this.filterContainer = new GridPane( );
		
		/* Initialize ColumnConstraints for the filter container */
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(25);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(5);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(30);
		ColumnConstraints col4 = new ColumnConstraints();
		col4.setPercentWidth(40);
		
		/* Add constraints to the filter container */
		filterContainer.getColumnConstraints().addAll(col1, col2, col3, col4);
		filterContainer.setHgap(5);
		filterContainer.setVgap(5);

		/* Add the elements to the filter container */
		filterContainer.add(categoryFilterLabel, 0, 0, 1, 1);
		filterContainer.add(separatorLabel1, 1, 0, 1, 1);
		filterContainer.add(categoryFilter, 2, 0, 1, 1);
		
		filterContainer.add(statusFilterLabel, 0, 1, 1, 1);
		filterContainer.add(separatorLabel2, 1, 1, 1, 1);
		filterContainer.add(this.statusFilter, 2, 1, 1, 1);
		
		GridPane.setHalignment(this.searchFilter, HPos.RIGHT);
		filterContainer.add(this.searchFilter, 3, 2, 1, 1);
		
		this.filterContainer.getStyleClass().addAll("spacing-5");
	}
	
	/**
	 * Initialize the products list TableView
	 */
	public void buildTabView()
	{
		/* Initialize columns */
		TableColumn productCol = new TableColumn("Produkt");
	    TableColumn categoryCol = new TableColumn("Produktgrouppe");
	    TableColumn preisCol = new TableColumn("€/Tag");
	    TableColumn statusCol = new TableColumn("Status");
	    TableColumn dateFromCol = new TableColumn("Leihdatum");
	    TableColumn dateToCol = new TableColumn("Rückgabedatum");
	    
	    productCol.setCellValueFactory(new PropertyValueFactory<ProductDetails, String>("productname"));
	    categoryCol.setCellValueFactory(new PropertyValueFactory<ProductDetails, String>("categorylabel"));
	    preisCol.setCellValueFactory(new PropertyValueFactory<ProductDetails, Float>("preis"));
	    statusCol.setCellValueFactory(new PropertyValueFactory<ProductDetails, String>("status"));
	    dateFromCol.setCellValueFactory(new PropertyValueFactory<ProductDetails, String>("datefrom"));
	    dateToCol.setCellValueFactory(new PropertyValueFactory<ProductDetails, String>("dateto"));
	    
	    this.tableView.setItems(this.filteredProductList);
	    this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		this.tableView.getColumns().addAll(productCol, categoryCol, preisCol, statusCol, dateFromCol, dateToCol);
		this.tableView.getStyleClass().add("client-over-view");
	}
	
	/**
	 * Build the footer that contains action Buttons like add, edit and remove
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public void buildFooter(MainContainer mainContainer)
	{
		Button newProductBt = new Button("Neues Produkt");
		Button editProductBt = new Button("Bearbeiten");
		Button removeProductBt = new Button("Löschen");
		
		editProductBt.disableProperty().bind(Bindings.isEmpty(this.tableView.selectionModelProperty().get().getSelectedCells()));
		removeProductBt.disableProperty().bind(Bindings.isEmpty(this.tableView.selectionModelProperty().get().getSelectedCells()));
		
		newProductBt.getStyleClass().addAll("btn", "spacing-15");
		editProductBt.getStyleClass().addAll("btn", "spacing-15");
		removeProductBt.getStyleClass().addAll("btn", "spacing-15");
		
		Helpers.addProductt(newProductBt, mainContainer);
		Helpers.updateProductt(editProductBt, this, mainContainer);
		Helpers.removeProduct(removeProductBt, this);
		
		this.footer = new HBox(newProductBt, editProductBt, removeProductBt);
		this.footer.getStyleClass().addAll("table-view-footer", "align-center");
	}
		
	/**
	 * Initialize the final GUI for the product over view
	 * @return : Group of elements
	 */
	public Group getView()
	{
		GridPane gPane = new GridPane();
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(10);
		RowConstraints row2 = new RowConstraints();
		RowConstraints row3 = new RowConstraints();
		RowConstraints row4 = new RowConstraints();
		row4.setPercentHeight(10);
		gPane.getRowConstraints().addAll(row1, row2, row3, row4);
		gPane.setHgap(5);
		gPane.setVgap(5);
		
		GridPane.setHalignment(this.title, HPos.CENTER);
		gPane.add(this.title, 0, 0);
		gPane.add(this.filterContainer, 0, 1);
		gPane.add(this.tableView, 0, 2);
		gPane.add(this.footer, 0, 3);

		return new Group(gPane);
	}
	
	/**
	 * Initialize the GUI of products for the selection in Dialog panel
	 * @return : Group of elements
	 */
	public Group getProductViewForSelectTableView()
	{
		/* Initialize the status filter to available, in order to list only the availble products*/
		this.multiFilter.add(new Pair<String, String>("status","verfügbar"));
		
		/* Initialize the GridPane for the main container*/
		GridPane gPane = new GridPane();
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(10);
		RowConstraints row2 = new RowConstraints();
		RowConstraints row3 = new RowConstraints();
		RowConstraints row4 = new RowConstraints();
		row4.setPercentHeight(10);
		gPane.getRowConstraints().addAll(row1, row2, row3, row4);
		gPane.setHgap(5);
		gPane.setVgap(5);
		
		this.filterContainer.getChildren().removeIf(node -> this.filterContainer.getRowIndex(node) == 1);
		this.tableView.getColumns().removeIf(item -> {
			return item.getText().contains("Leihdatum") ||  item.getText().contains("Rückgabedatum") ||  item.getText().contains("Status") ;
		});
		
		GridPane.setHalignment(this.title, HPos.CENTER);
		gPane.add(this.title, 0, 0);
		gPane.add(this.filterContainer, 0, 1);
		gPane.add(this.tableView, 0, 2);

		return new Group(gPane);
	}
	
	/**
	 * Initialize the GUI for Date View
	 * @return
	 */
	public Group getProductSetDatumView()
	{
		this.initDates();
		Label title = new Label("Datum");
		Label fromLabel = new Label("Leihdatum");
		Label toLabel = new Label("Rückgabedatum");
		Label separator1 = new Label(":");
		Label separator2 = new Label(":");
		GridPane gPane = new GridPane();
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(30);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(5);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(30);
		
		gPane.getColumnConstraints().addAll(col1, col2, col3);
		gPane.setHgap(5);
		gPane.setVgap(5);
		
		GridPane.setHalignment(title, HPos.CENTER);
		gPane.add(title, 0, 0, 3, 1);
		
		gPane.add(fromLabel, 0, 1, 1, 1);
		gPane.add(separator1, 1, 1, 1, 1);
		gPane.add(this.dateFrom, 2, 1, 1, 1);
		
		gPane.add(toLabel, 0, 2, 1, 1);
		gPane.add(separator2, 1, 2, 1, 1);
		gPane.add(this.dateTo, 2, 2, 1, 1);
		return new Group(gPane);
	}
	
}
