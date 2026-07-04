package buttonHandlers;

import java.util.Optional;

import client.Client;
import gui.CategoryOverView;
import gui.ClientOverView;
import gui.InvoiceView;
import gui.MainContainer;
import gui.OverView;
import gui.ProductOverView;
import gui.RentOverview;
import gui.ReturnOverView;
import gui.UpdateCategoryView;
import gui.UpdateClientView;
import gui.UpdateProductView;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import main.Leihaus;
import product.Category;
import product.ProductDetails;

/**
 * Class Helper for EventsHandler
 */
public class Helpers {

	private static  Alert warningAlert = new Alert(AlertType.WARNING); // Initialize the WARNING Dialog pane
	private static Alert confirmtionAlert; // Initialize the CONFIRM Dialog pane
	
	/******************  Product View Events *******************/
	
	/**
	 * Initialize the EventHandler to add a client
	 * @param bt : a given add button
	 * @param mainContainer : the main container panel
	 */
	public static void addClient(Button bt, MainContainer mainContainer)
	{
		bt.setOnAction(action -> {
			mainContainer.setContent(new UpdateClientView(mainContainer).getView());
		});
	}
	
	/**
	 * Initialize the EventHandler to update a client
	 * @param bt : a given edit button
	 * @param clView : the GUI for client view
	 * @param mainContainer : the main container panel
	 */
	public static void updateClient(Button bt, ClientOverView clView, MainContainer mainContainer)
	{
		bt.setOnAction(action -> {
			Client client = (Client) clView.tableView.getSelectionModel().getSelectedItem();
			
			/* Alert the user in case of no selected item */
			if(client == null) {
				warningAlert = new Alert(AlertType.WARNING);
				warningAlert.setGraphic(null);
				warningAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
				warningAlert.setHeaderText("Wählen Sie bitte einen Kunden aus!");
				Optional<ButtonType> option = warningAlert.showAndWait();
			} else // Otherwise open the client view on edit mode
			{
				mainContainer.setContent(new UpdateClientView(client, mainContainer).getView());
			}
		});
	}
	
	/**
	 * Initialize the EventHandler to remove a client
	 * @param bt : a given remove button
	 * @param clView : the GUI for client view
	 */
	public static void removeClient(Button bt, ClientOverView clView)
	{
		bt.setOnAction(action -> {
			Client client = (Client) clView.tableView.getSelectionModel().getSelectedItem();
			
			/* Alert the user in case of no selected item */
			if(client == null) {
				warningAlert = new Alert(AlertType.WARNING);
				warningAlert.setGraphic(null);
				warningAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
				warningAlert.setHeaderText("Wählen Sie bitte einen Kunden aus!");
				Optional<ButtonType> option = warningAlert.showAndWait();
				
			} else  // Otherwise ask the user to confirm the delete
			{
				String fullname = client.getFirstname() + " " + client.getLastname();
				confirmtionAlert = new Alert(AlertType.CONFIRMATION);
				confirmtionAlert.setGraphic(null);
				confirmtionAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
				confirmtionAlert.setTitle("Kunde löschen");
				confirmtionAlert.setHeaderText("Möchten Sie den Kunden " + fullname + " wirklich löschen ?");
				Optional<ButtonType> option = confirmtionAlert.showAndWait();
				
				if(option.get() == ButtonType.OK) {
					try {
						Leihaus.db.removeClient(client);
						clView.initClientList(null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/******************  Product View Events *******************/
	
	/**
	 * Initialize the EventHandler to add a product
	 * @param bt : a given edit button
	 * @param mainContainer : the main container panel
	 */
	public static void addProductt(Button bt, MainContainer mainContainer)
	{
		bt.setOnAction(action -> {
			try {
				mainContainer.setContent(new UpdateProductView(mainContainer).getView());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Initialize the EventHandler to update a product
	 * @param bt : a given edit button
	 * @param productView : the GUI for product view
	 * @param mainContainer : the main container panel
	 */
	public static void updateProductt(Button bt, ProductOverView productView, MainContainer mainContainer)
	{
		bt.setOnAction(action -> {
			ProductDetails product = (ProductDetails) productView.tableView.getSelectionModel().getSelectedItem();
			
			/* Alert the user in case of no selected item */
			if(product == null) {
				warningAlert = new Alert(AlertType.WARNING);
				warningAlert.setGraphic(null);
				warningAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
				warningAlert.setHeaderText("Wählen Sie bitte ein Produkt aus!");
				Optional<ButtonType> option = warningAlert.showAndWait();
			} else // Otherwise open the product view on edit mode
			{
				try {
					mainContainer.setContent(new UpdateProductView(product, mainContainer).getView());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Initialize the EventHandler to delete a product
	 * @param bt : a given remove button
	 * @param productView : the GUI for product view
	 */
	public static void removeProduct(Button bt, ProductOverView productView)
	{
		bt.setOnAction(action -> {
			ProductDetails product = (ProductDetails) productView.tableView.getSelectionModel().getSelectedItem();

			/* Alert the user in case of no selected item */
			if(product == null) {
				warningAlert = new Alert(AlertType.WARNING);
				warningAlert.setGraphic(null);
				warningAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
				warningAlert.setHeaderText("Wählen Sie bitte ein Produkt aus!");
				Optional<ButtonType> option = warningAlert.showAndWait();
			} else  // Otherwise ask the user to confirm the delete
			{
				String productname = product.getProductname();
				confirmtionAlert = new Alert(AlertType.CONFIRMATION);
				confirmtionAlert.setGraphic(null);
				confirmtionAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
				confirmtionAlert.setTitle("Kunde löschen");
				confirmtionAlert.setHeaderText("Möchten Sie das Produkt " + productname + " wirklich löschen ?");
				Optional<ButtonType> option = confirmtionAlert.showAndWait();
				
				if(option.get() == ButtonType.OK) {
					try {
						Leihaus.db.removeProduct(product);
						productView.productList.clear();
						productView.productList.addAll(Leihaus.db.getProductOverView());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	/******************  Rent View Events *******************/

	/**
	 * Initialize the EventHandler to select a client from a list for the rent view 
	 * @param bt : a given remove button
	 * @param rentOverview : the GUI for a rent view
	 */
	public static void selectClientForRent(Button bt, RentOverview rentOverview)
	{
		bt.setOnAction(action-> {
			try {
				ClientOverView clientList = new ClientOverView();
				
				/* Open the clients list in a CONFIRM Dialog */
				confirmtionAlert = new Alert(AlertType.CONFIRMATION);
				confirmtionAlert.setGraphic(null);
				confirmtionAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
				confirmtionAlert.setHeaderText("");
				confirmtionAlert.getDialogPane().setContent(clientList.getClientViewForSelectTableView());
				
				Optional<ButtonType> option = confirmtionAlert.showAndWait();
				
				if(option.get() == ButtonType.OK) {
					try {
						Client selectedClient = clientList.tableView.getSelectionModel().getSelectedItem();
						rentOverview.clientList.clear();
						rentOverview.clientList.add(selectedClient);
						rentOverview.buildClientLabel();
						rentOverview.buildClientContainer();
						rentOverview.rebuildView();
						System.out.println(selectedClient.getFirstname());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Initialize the EventHandler to select a product from a TableView for the rent view 
	 * @param bt : a given remove button
	 * @param rentOverview : the GUI for a rent view
	 */
	public static void selecProductForRent(Button bt, RentOverview rentOverview)
	{
		bt.setOnAction(action-> {
			try {
				/* Open the products list in a TableView in a CONFIRM Dialog */
				confirmtionAlert = new Alert(AlertType.CONFIRMATION);
				confirmtionAlert.setGraphic(null);
				confirmtionAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
				confirmtionAlert.setHeaderText("");
				confirmtionAlert.getDialogPane().setContent(rentOverview.productListView.getProductViewForSelectTableView());
				Optional<ButtonType> option = confirmtionAlert.showAndWait();
				
				if(option.get() == ButtonType.OK) {
						ProductDetails selectedProduct = rentOverview.productListView.tableView.getSelectionModel().getSelectedItem();
						if(selectedProduct != null) {
							rentOverview.productListView.productList.remove(selectedProduct);
							/* After select product open a CONFIRM Dialog to set the rent period (date from, date to)  */
							confirmtionAlert = new Alert(AlertType.CONFIRMATION);
							confirmtionAlert.setGraphic(null);
							confirmtionAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
							confirmtionAlert.setHeaderText("");
							confirmtionAlert.getDialogPane().setContent(rentOverview.productListView.getProductSetDatumView());
							Optional<ButtonType> option2 = confirmtionAlert.showAndWait();
							
							if(option2.get() == ButtonType.OK) {
								selectedProduct.setDatefrom(rentOverview.productListView.dateFrom.getValue().toString());
								selectedProduct.setDateto(rentOverview.productListView.dateTo.getValue().toString());
								selectedProduct.computePeriode();
								rentOverview.productList.add(selectedProduct);
								rentOverview.computeTotalPrice();
								rentOverview.rebuildView();
							}
						}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Initialize the EventHandler to create the invoice 
	 * @param bt : a given remove button
	 * @param rentOverview : the GUI for a rent view
	 */
	public static void createInvoice(Button bt, RentOverview rentOverview)
	{
		bt.setOnAction(action-> {
			try {
				if(rentOverview.saveBt.isVisible()) {
					Leihaus.db.saveRent(rentOverview.clientList.get(0), rentOverview.productList);
				}
				rentOverview.renderInvoiceView();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Initialize the EventHandler to save a rent 
	 * @param bt : a given remove button
	 * @param rentOverview : the GUI for a rent view
	 */
	public static void saveRent(Button bt, RentOverview rentOverview)
	{
		bt.setOnAction(action-> {
			try {
				Leihaus.db.saveRent(rentOverview.clientList.get(0), rentOverview.productList);
				rentOverview.saveBt.visibleProperty().set(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/******************  Return View Events *******************/

	/**
	 * Initialize the EventHandler to select a client from a list for the return product view 
	 * @param bt : a given remove button
	 * @param returnOverView : the GUI for a return product view
	 */
	public static void selectClientForReturn(Button bt, ReturnOverView returnOverView)
	{
		bt.setOnAction(action-> {
			try {
				ClientOverView clientList = new ClientOverView(new Pair("filter", "hasRents"));
				confirmtionAlert = new Alert(AlertType.CONFIRMATION);
				confirmtionAlert.setGraphic(null);
				confirmtionAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
				confirmtionAlert.setHeaderText("");
				confirmtionAlert.getDialogPane().setContent(clientList.getClientViewForSelectTableView());
				
				Optional<ButtonType> option = confirmtionAlert.showAndWait();
				
				if(option.get() == ButtonType.OK) {
					try {
						Client selectedClient = clientList.tableView.getSelectionModel().getSelectedItem();
						returnOverView.clientList.clear();
						returnOverView.clientList.add(selectedClient);
						returnOverView.productList.clear();
						returnOverView.productList.addAll(Leihaus.db.getRentedProductsByClient(selectedClient));
						returnOverView.buildClientLabel();
						returnOverView.buildClientContainer();
						returnOverView.rebuildView();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Initialize the EventHandler to select a client from a list for the return product view 
	 * @param bt : a given remove button
	 * @param returnOverView : the GUI for a return product view
	 */
	public static void returnProductBt(Button bt, ReturnOverView returnOverView)
	{
		bt.setOnAction(action-> {
			ProductDetails product = (ProductDetails) returnOverView.productsTableView.getSelectionModel().getSelectedItem();
			
			if(product == null) {
				warningAlert = new Alert(AlertType.WARNING);
				warningAlert.setGraphic(null);
				warningAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
				warningAlert.setHeaderText("Wählen Sie bitte ein Produkt aus!");
				Optional<ButtonType> option = warningAlert.showAndWait();
			} else {
				try {
					Leihaus.db.updateRent(product.getRentId(), new Pair("status", "returned"));
					returnOverView.productList.remove(product);
					if(returnOverView.productList.size() < 1) {
						returnOverView.clientList.clear();
						returnOverView.buildClientContainer();
						returnOverView.rebuildView();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		//	try {}
		});
	}
	
	/******************  Over View Events *******************/

	/**
	 * Initialize the EventHandler to show details 
	 * @param bt : a given remove button
	 * @param overView : the GUI for the over view
	 */
	public static void showRentDetails(Button bt, OverView overView)
	{
		bt.setOnAction(action-> {
			int clientId = overView.rentTableView.getSelectionModel().getSelectedItem().getCid();
			ReturnOverView returnOverView = new ReturnOverView(overView.mainContainer);
			returnOverView.initClient(clientId);
			overView.mainContainer.setContent(returnOverView.getView());
			
		});
	}

	/******************  Categories View Events *******************/

	/**
	 * Initialize the EventHandler to add a category
	 * @param bt : a given add button
	 * @param mainContainer : the main container panel
	 */
	public static void addCategory(Button bt, MainContainer mainContainer) 
	{
		bt.setOnAction(action -> {
			mainContainer.setContent(new UpdateCategoryView(mainContainer).getView());
		});
	}

	/**
	 * Initialize the EventHandler to update a category
	 * @param bt : a given edit button
	 * @param clView : the GUI for category view
	 * @param mainContainer : the main container panel
	 */
	public static void updateCategory(Button bt, CategoryOverView categoryOverView, MainContainer mainContainer) 
	{
		bt.setOnAction(action -> {
			Category category = (Category) categoryOverView.tableView.getSelectionModel().getSelectedItem();
			
			/* Alert the user in case of no selected item */
			if(category == null) {
				warningAlert = new Alert(AlertType.WARNING);
				warningAlert.setGraphic(null);
				warningAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
				warningAlert.setHeaderText("Wählen Sie bitte eine Kategorie aus!");
				Optional<ButtonType> option = warningAlert.showAndWait();
			} else // Otherwise open the category view on edit mode
			{
				mainContainer.setContent(new UpdateCategoryView(category, mainContainer).getView());
			}
		});
	}

	/**
	 * Initialize the EventHandler to remove a category
	 * @param bt : a given remove button
	 * @param clView : the GUI for category view
	 */
	public static void removeCategory(Button bt, CategoryOverView categoryView) 
	{
		bt.setOnAction(action -> {
			Category category = (Category) categoryView.tableView.getSelectionModel().getSelectedItem();
			
			/* Alert the user in case of no selected item */
			if(category == null) {
				warningAlert = new Alert(AlertType.WARNING);
				warningAlert.setGraphic(null);
				warningAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
				warningAlert.setHeaderText("Wählen Sie bitte eine Kategorie aus!");
				Optional<ButtonType> option = warningAlert.showAndWait();
				
			} else  // Otherwise ask the user to confirm the delete
			{
				String label = category.getLabel();
				confirmtionAlert = new Alert(AlertType.CONFIRMATION);
				confirmtionAlert.setGraphic(null);
				confirmtionAlert.getDialogPane().setPadding(new Insets(20, 20, 20, 10));
				confirmtionAlert.setTitle("Kunde löschen");
				confirmtionAlert.setHeaderText("Möchten Sie die Kategorie '" + label + "' wirklich löschen ?");
				Optional<ButtonType> option = confirmtionAlert.showAndWait();
				
				if(option.get() == ButtonType.OK) {
					try {
						Leihaus.db.removeCategory(category);
						categoryView.initCategoriesList();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
}
