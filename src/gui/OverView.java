package gui;

import java.sql.SQLException;

import buttonHandlers.FilterHelpers;
import buttonHandlers.Helpers;
import client.Client;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.util.Pair;
import main.Leihaus;
import product.Category;
import product.ProductDetails;
import product.Rent;
import product.Status;

public class OverView {

	public MainContainer mainContainer;
	Label title;
	TextField searchClientFilter;
	TextField searchProductFilter;

	public DatePicker dateFrom = new DatePicker();
	public DatePicker dateTo = new DatePicker();
	
	public GridPane filterContainer ;
	public GridPane rentContainer ;
	Button showDetails;
	HBox footer;

	public TableView<Rent> rentTableView = new TableView<Rent>();
	public ObservableList<Pair<String,String>> multiFilter = FXCollections.observableArrayList();
	public ObservableList<Rent> rentList = FXCollections.observableArrayList();
	private FilteredList<Rent> filteredRentList = new FilteredList<>(this.rentList, rent -> true);
	
	/**
	 * Constructor for the GUI OverView
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public OverView(MainContainer mainContainer)
	{
		try {
			this.rentList.addAll(Leihaus.db.getRentList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.mainContainer = mainContainer;
		this.buildTitle();
		this.buildFilter();
		this.buildTabView();
		this.buildFooter();
	}
	
	/**
	 * Build the title for GUI
	 */
	public void buildTitle()
	{
		this.title = new Label("Übersicht");
		this.title.getStyleClass().add("head-title");
	}
	
	/**
	 * Prepare the filter elements
	 */
	public void buildFilter()
	{
		this.searchClientFilter = new TextField();
		this.searchClientFilter.setPromptText("Kunde suchen ...");
		this.searchClientFilter.getStyleClass().addAll("filter");
		
		this.searchProductFilter = new TextField();
		this.searchProductFilter.setPromptText("Produkt suchen ...");
		this.searchProductFilter.getStyleClass().addAll("filter");
		
		this.buildFilterHandler();
		this.buildFiterView();
	}
	
	/**
	 * Initialize the Filter EventHandler for filter fields
	 */
	public void buildFilterHandler() {
		
		this.searchProductFilter.textProperty().addListener((observable, oldVal, newVal) -> {
			this.multiFilter.remove(new Pair<String, String>("product",oldVal));
			this.multiFilter.add(new Pair<String, String>("product",newVal));
		});
		
		this.searchClientFilter.textProperty().addListener((observable, oldVal, newVal) -> {
			this.multiFilter.remove(new Pair<String, String>("client",oldVal));
			this.multiFilter.add(new Pair<String, String>("client",newVal));
		});
		
		this.dateFrom.valueProperty().addListener((observable, oldVal, newVal) -> {
			String oldVal2 = "";
			if(oldVal != null) {
				oldVal2 = oldVal.toString();
			}
			
			System.out.println(oldVal + "vvvvvvv");
			this.multiFilter.remove(new Pair<String, String>("dateFrom",oldVal2));
			this.multiFilter.add(new Pair<String, String>("dateFrom",newVal.toString()));
		});
		
		this.dateTo.valueProperty().addListener((observable, oldVal, newVal) -> {
			String oldVal2 = "";
			if(oldVal != null) {
				oldVal2 = oldVal.toString();
			}
			
			this.multiFilter.remove(new Pair<String, String>("dateTo",oldVal2));
			this.multiFilter.add(new Pair<String, String>("dateTo",newVal.toString()));
		});
		
		
		this.multiFilter.addListener((Change<? extends Pair<String, String>> change)->{
			while(change.next()) {
				if(change.wasAdded()) {
					this.filteredRentList.setPredicate(rent -> {
						return FilterHelpers.multiFilter(rent, change.getList());
					});
				}
			}
		});
		
		this.multiFilter.add(new Pair<String, String>("status","ausgeliehen"));
		
	}
	
	/**
	 * Prepare the filter View
	 */
	public void buildFiterView()
	{
		this.filterContainer = new GridPane();
		
		Label fromLabel = new Label("Von");
		fromLabel.getStyleClass().addAll("filter-label");
		Label separator1 = new Label(":");
		separator1.getStyleClass().addAll("filter-label");

		Label toLabel = new Label("Bis");
		toLabel.getStyleClass().addAll("filter-label");
		Label separator2 = new Label(":");
		separator2.getStyleClass().addAll("filter-label");
		

		this.filterContainer = new GridPane( );
		
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(8);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(2);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(35);
		ColumnConstraints col4 = new ColumnConstraints();
		col4.setPercentWidth(10);
		ColumnConstraints col5 = new ColumnConstraints();
		col5.setPercentWidth(8);
		ColumnConstraints col6 = new ColumnConstraints();
		col6.setPercentWidth(2);
		ColumnConstraints col7 = new ColumnConstraints();
		col7.setPercentWidth(35);
		//filterContainer.getStyleClass().addAll("border");
		//filterContainer.setStyle("-fx-grid-lines-visible: true");
		filterContainer.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6, col7);
		filterContainer.setHgap(5);
		filterContainer.setVgap(5);
		
		GridPane.setHalignment(this.searchClientFilter, HPos.LEFT);
		filterContainer.add(this.searchClientFilter, 0, 0, 3, 1);
		
		filterContainer.add(new Label(""), 3, 0, 1, 1);

		GridPane.setHalignment(this.searchProductFilter, HPos.RIGHT);
		filterContainer.add(this.searchProductFilter, 4, 0, 3, 1);
		
		filterContainer.add(fromLabel, 0, 1, 1, 1);
		filterContainer.add(separator1, 1, 1, 1, 1);
		filterContainer.add(this.dateFrom, 2, 1, 1, 1);
		
		filterContainer.add(new Label(""), 3, 1, 1, 1);
		
		filterContainer.add(toLabel, 4, 1, 1, 1);
		filterContainer.add(separator2, 5, 1, 1, 1);
		filterContainer.add(this.dateTo, 6, 1, 1, 1);
		
		filterContainer.add(new Label(""), 0, 2, 7, 1);
	}
	
	/**
	 * Initialize the products list TableView
	 */
	public void buildTabView()
	{
		TableColumn clientCol = new TableColumn("Kunde");
		TableColumn productCol = new TableColumn("Produkt");
	    TableColumn preisCol = new TableColumn("€/Tag");
	    TableColumn daysCol = new TableColumn("Tage");
	    TableColumn dateFromCol = new TableColumn("Leihdatum");
	    TableColumn dateToCol = new TableColumn("Rückgabedatum");
	    
	    clientCol.setCellValueFactory(new PropertyValueFactory<Rent, String>("clientname"));
	    productCol.setCellValueFactory(new PropertyValueFactory<Rent, String>("productname"));
	    preisCol.setCellValueFactory(new PropertyValueFactory<Rent, Float>("preis"));
	    daysCol.setCellValueFactory(new PropertyValueFactory<Rent, String>("periode"));
	    dateFromCol.setCellValueFactory(new PropertyValueFactory<Rent, String>("datefrom"));
	    dateToCol.setCellValueFactory(new PropertyValueFactory<Rent, String>("dateto"));
	    
	    this.rentTableView.setItems(this.filteredRentList);
		this.rentTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		this.rentTableView.setMaxHeight(250);
		this.rentTableView.getColumns().addAll(clientCol,productCol, preisCol, daysCol, dateFromCol, dateToCol);
	}
	
	/**
	 * Build the footer that contains action Buttons like add, edit and remove
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public void buildFooter()
	{
		this.showDetails = new Button("Details");
		this.showDetails.getStyleClass().addAll("btn", "spacing-15");
		this.showDetails.disableProperty().bind(Bindings.isEmpty(this.rentTableView.selectionModelProperty().get().getSelectedCells()));
		Helpers.showRentDetails(this.showDetails, this);
		
		this.footer = new HBox(this.showDetails);
		this.footer.getStyleClass().addAll("table-view-footer", "align-right");
	}
	
	/**
	 * Initialize the final GUI for the over view
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
		gPane.add(this.filterContainer, 0, 1);
		gPane.add(this.rentTableView, 0, 2);
		gPane.add(this.footer, 0, 4);

		return new Group(gPane);
	}
	
	
}
