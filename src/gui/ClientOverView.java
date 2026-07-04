package gui;

import java.sql.SQLException;

import buttonHandlers.FilterHelpers;
import buttonHandlers.Helpers;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.Group;
import javafx.util.Pair;
import main.Leihaus;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import client.Client;

public class ClientOverView {
	
	Label title;
	TextField filter;
	HBox filterContainer;
	HBox footer;
	public TableView<Client> tableView = new TableView<Client>();
	
	
	public ObservableList<Client> clientsList = FXCollections.observableArrayList();
	private FilteredList<Client> filteredClientList = new FilteredList<>(this.clientsList, cl -> true);
	
	/**
	 * Constructor for the GUI ClientOverView
	 * @throws SQLException : needed to throw errors from SQL
	 * @throws Exception : needed to throw global errors
	 */
	public ClientOverView() throws SQLException, Exception
	{
		this.initClientList(null);
		this.buildTitle();
		this.buildFiter();
		this.buildTabview();
	}

	/**
	 * Constructor for the GUI ClientOverView
	 * @param filter : contain key value of condition to get filtered clients from DB
	 * @throws SQLException
	 * @throws Exception
	 */
	public ClientOverView(Pair<String, String> filter) throws SQLException, Exception
	{
		this.initClientList(filter);
		this.buildTitle();
		this.buildFiter();
		this.buildTabview();
	}
	
	/**
	 * Constructor for the GUI ClientOverView
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 * @throws SQLException : needed to throw errors from SQL
	 * @throws Exception : needed to throw global errors
	 */
	public ClientOverView(MainContainer mainContainer) throws SQLException, Exception
	{
		this.initClientView(mainContainer);
	}
	
	/**
	 * Initialize the overview GUI for the list of clients 
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 * @throws SQLException : needed to throw errors from SQL
	 * @throws Exception : needed to throw global errors
	 */
	public void initClientView(MainContainer mainContainer) throws SQLException, Exception
	{
		this.initClientList(null);
		this.buildTitle();
		this.buildFiter();
		this.buildTabview();
		this.buildFooter(mainContainer);
	}
	
	/**
	 * Initialize the list of clients
	 * Get all rented filtered clients from DB
	 * @throws SQLException : needed to throw errors from SQL
	 * @throws Exception : needed to throw global errors
	 */
	public void initClientList(Pair<String, String> filter) throws SQLException, Exception
	{
		clientsList.clear();
		clientsList.addAll(Leihaus.db.getClients(filter));
		
	}
	
	/**
	 * Build the title for GUI
	 */
	public void buildTitle()
	{
		this.title = new Label("Kunden");
		this.title.getStyleClass().add("head-title");
	}
	
	/**
	 * Prepare the filter elements
	 */
	public void buildFiter()
	{
		this.filter = new TextField();
		this.filter.setPromptText("Search...");
		this.filter.getStyleClass().addAll("filter");
		
		this.filter.textProperty().addListener((observable, oldVal, newVal) -> {
			
			this.filteredClientList.setPredicate(client -> {
				return FilterHelpers.clientNameFilter(client.getFirstname(), newVal);
			});
		});
		
		this.filterContainer = new HBox(this.filter);
		this.filterContainer.getStyleClass().addAll("filter-container", "spacing-15");
	}
	
	/**
	 * Initialize the clients list TableView
	 */
	public void buildTabview()
	{
		TableColumn firstNameCol = new TableColumn("Vormane");
	    TableColumn lastnameCol = new TableColumn("Name");
	    TableColumn addressCol = new TableColumn("Anschrift");
	    TableColumn plzCol = new TableColumn("PLZ");
	    TableColumn cityCol = new TableColumn("Ort");
	    TableColumn telCol = new TableColumn("Tel");
	    
		firstNameCol.setCellValueFactory(new PropertyValueFactory<Client, String>("firstname"));
		lastnameCol.setCellValueFactory(new PropertyValueFactory<Client, String>("lastname"));
		addressCol.setCellValueFactory(new PropertyValueFactory<Client, String>("address"));
		plzCol.setCellValueFactory(new PropertyValueFactory<Client, String>("plz"));
		cityCol.setCellValueFactory(new PropertyValueFactory<Client, String>("city"));
		telCol.setCellValueFactory(new PropertyValueFactory<Client, String>("tel"));
		
		this.tableView.setItems(this.filteredClientList);
		this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		this.tableView.getColumns().addAll(firstNameCol, lastnameCol, addressCol, plzCol, cityCol, telCol);
		this.tableView.getStyleClass().add("client-over-view");
	}
	
	/**
	 * Build the footer that contains action Buttons like add, edit and remove
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public void buildFooter(MainContainer mainContainer)
	{
		Button newClientBt = new Button("Neuer Kunde");
		Button editClientBt = new Button("Bearbeiten");
		Button removeClientBt = new Button("Löschen");
		
		editClientBt.disableProperty().bind(Bindings.isEmpty(this.tableView.selectionModelProperty().get().getSelectedCells()));
		removeClientBt.disableProperty().bind(Bindings.isEmpty(this.tableView.selectionModelProperty().get().getSelectedCells()));
		
		
		newClientBt.getStyleClass().addAll("btn", "spacing-15");
		editClientBt.getStyleClass().addAll("btn", "spacing-15");
		removeClientBt.getStyleClass().addAll("btn", "spacing-15");
		
		Helpers.addClient(newClientBt, mainContainer);
		Helpers.updateClient(editClientBt, this, mainContainer);
		Helpers.removeClient(removeClientBt, this);
		
		this.footer = new HBox(newClientBt, editClientBt, removeClientBt);
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
	 * Initialize the GUI of clients for the selection in Dialog panel
	 * @return : Group of elements
	 */
	public Group getClientViewForSelectTableView()
	{
		GridPane gPane = new GridPane();
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(10);
		RowConstraints row2 = new RowConstraints();
		RowConstraints row3 = new RowConstraints();
		gPane.getRowConstraints().addAll(row1, row2, row3);
		gPane.setHgap(5);
		gPane.setVgap(5);
		
		GridPane.setHalignment(this.title, HPos.CENTER);
		gPane.add(this.title, 0, 0);
		gPane.add(this.filterContainer, 0, 1);
		gPane.add(this.tableView, 0, 2);
		
		return new Group(gPane);
	}
}
