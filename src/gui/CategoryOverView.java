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
import product.Category;

public class CategoryOverView {

	Label title;
	TextField filter;
	HBox filterContainer;
	HBox footer;
	public TableView<Category> tableView = new TableView<Category>();
	
	public ObservableList<Category> categoriesList = FXCollections.observableArrayList();
	private FilteredList<Category> filteredCategoriesList = new FilteredList<>(this.categoriesList, cl -> true);
	
	/**
	 * Constructor for the GUI CategoryOverView
	 * @throws SQLException : needed to throw errors from SQL
	 * @throws Exception : needed to throw global errors
	 */
	public CategoryOverView() throws SQLException, Exception
	{
		this.initCategoriesList();
		this.buildTitle();
		this.buildFiter();
		this.buildTabview();
	}

	/**
	 * Constructor for the GUI CategoryOverView
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 * @throws SQLException : needed to throw errors from SQL
	 * @throws Exception : needed to throw global errors
	 */
	public CategoryOverView(MainContainer mainContainer) throws SQLException, Exception
	{
		this.initCategoryView(mainContainer);
	}
	
	/**
	 * Initialize the overview GUI for the list of the categories 
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 * @throws SQLException : needed to throw errors from SQL
	 * @throws Exception : needed to throw global errors
	 */
	public void initCategoryView(MainContainer mainContainer) throws SQLException, Exception
	{
		this.initCategoriesList();
		this.buildTitle();
		this.buildFiter();
		this.buildTabview();
		this.buildFooter(mainContainer);
	}
	
	/**
	 * Initialize the list of categories
	 * Get all categories from DB
	 * @throws SQLException : needed to throw errors from SQL
	 * @throws Exception : needed to throw global errors
	 */
	public void initCategoriesList() throws SQLException, Exception
	{
		categoriesList.clear();
		categoriesList.addAll(Leihaus.db.getCategories());
		
	}
	
	/**
	 * Build the title for GUI
	 */
	public void buildTitle()
	{
		this.title = new Label("Kategorien");
		this.title.getStyleClass().add("head-title");
	}
	
	/**
	 * Prepare the filter elements
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void buildFiter()
	{
		this.filter = new TextField();
		this.filter.setPromptText("Search...");
		this.filter.getStyleClass().addAll("filter");
		
		this.filter.textProperty().addListener((observable, oldVal, newVal) -> {
			
			this.filteredCategoriesList.setPredicate(category -> {
				return FilterHelpers.categoryNameFilter(category.getLabel(), newVal);
			});
		});
		
		this.filterContainer = new HBox(this.filter);
		this.filterContainer.getStyleClass().addAll("filter-container", "spacing-15");
	}
	
	/**
	 * Initialize the categories list TableView
	 */
	public void buildTabview()
	{
		TableColumn firstNameCol = new TableColumn("Bezeichnung");
	    
		firstNameCol.setCellValueFactory(new PropertyValueFactory<Category, String>("label"));
		
		this.tableView.setItems(this.filteredCategoriesList);
		this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		this.tableView.getColumns().addAll(firstNameCol);
		this.tableView.getStyleClass().add("client-over-view");
	}
	
	/**
	 * Build the footer that contains action Buttons like add, edit and remove
	 * @param mainContainer : is the main Panel which contain all elements like Titel, Filter, TableView and Filter
	 */
	public void buildFooter(MainContainer mainContainer)
	{
		Button newCategoryBt = new Button("Neues Kategorie");
		Button editCategoryBt = new Button("Bearbeiten");
		Button removeCategoryBt = new Button("Löschen");
		
		editCategoryBt.disableProperty().bind(Bindings.isEmpty(this.tableView.selectionModelProperty().get().getSelectedCells()));
		removeCategoryBt.disableProperty().bind(Bindings.isEmpty(this.tableView.selectionModelProperty().get().getSelectedCells()));
		
		
		newCategoryBt.getStyleClass().addAll("btn", "spacing-15");
		editCategoryBt.getStyleClass().addAll("btn", "spacing-15");
		removeCategoryBt.getStyleClass().addAll("btn", "spacing-15");
		
		Helpers.addCategory(newCategoryBt, mainContainer);
		Helpers.updateCategory(editCategoryBt, this, mainContainer);
		Helpers.removeCategory(removeCategoryBt, this);
		
		this.footer = new HBox(newCategoryBt, editCategoryBt, removeCategoryBt);
		this.footer.getStyleClass().addAll("table-view-footer", "align-center");
	}
	
	/**
	 * Initialize the final GUI for the categories over view
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
	
}
