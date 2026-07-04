package gui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import main.Leihaus;
import javafx.scene.layout.HBox;

import client.Client;

public class UpdateClientView {
	Label title;
	VBox form;
	HBox footer;
	Client client;
	TextField firstname;
	TextField lastname;
	TextField address;
	TextField plz;
	TextField city;
	TextField tel;
	
	/**
	 * Constructor for the GUI UpdateClientView
	 * @param mainContainer : is the main Panel which contain all elements like Title, Filter, TableView and Filter
	 */
	public UpdateClientView(MainContainer mainContainer) {
		this.client = new Client();
		this.initView(mainContainer);
	}
	
	/**
	 * Constructor for the GUI UpdateClientView
	 * @param client : client Object
	 * @param mainContainer : is the main Panel which contain all elements like Title, Filter, TableView and Filter
	 */
	public UpdateClientView(Client client ,MainContainer mainContainer) {
		this.client = client;
		this.initView(mainContainer);
	}
	
	/**
	 * Initialize the GUI for a client View
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
		this.title = new Label("Neuer Kunde");
		this.title.getStyleClass().add("head-title");
	}
	
	/**
	 * Build the Form GUI for a client
	 */
	public void buildForm()
	{
		this.firstname = new TextField();
		this.firstname.setPromptText("Vorname");
		this.firstname.setText(this.client.getFirstname());
		this.firstname.getStyleClass().addAll("input", "spacing-5");
		
		this.lastname = new TextField();
		this.lastname.setPromptText("Name");
		this.lastname.setText(this.client.getLastname());
		this.lastname.getStyleClass().addAll("input", "spacing-5");
		
		this.address = new TextField();
		this.address.setPromptText("Anschrift");
		this.address.setText(this.client.getAddress());
		this.address.getStyleClass().addAll("input", "spacing-5");
		
		this.plz = new TextField();
		this.plz.setPromptText("plz");
		this.plz.setText(this.client.getPlz());
		this.plz.getStyleClass().addAll("input", "spacing-5");

		this.city = new TextField();
		this.city.setPromptText("Ort");
		this.city.setText(this.client.getCity());
		this.city.getStyleClass().addAll("input", "spacing-5");

		this.tel = new TextField();
		this.tel.setPromptText("Tel");
		this.tel.setText(this.client.getTel());
		this.tel.getStyleClass().addAll("input", "spacing-5");
		
		this.form = new VBox(this.firstname, this.lastname, this.address, this.plz, this.city, this.tel);
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
				if(this.client.getId() == 0) {  // add clien
					Leihaus.db.addClient(this.saveClient());
				} else { // update client
					Leihaus.db.updateClient(this.saveClient());
				}
				// change the GUI view
				mainContainer.setContent(new ClientOverView(mainContainer).getView());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		/* Initialize the EventHandler for cancel Button*/
		cancel.setOnAction(action -> {
			try {
				mainContainer.setContent(new ClientOverView(mainContainer).getView());
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
	public Client saveClient()
	{
		this.client.setFirstname(this.firstname.getText());
		this.client.setLastname(this.lastname.getText());
		this.client.setAddress(this.address.getText());
		this.client.setPlz(this.plz.getText());
		this.client.setCity(this.city.getText());
		this.client.setTel(this.tel.getText());
		
		return this.client;
	}
	
	/**
	 * Initialize the final GUI for the UpdateClientView
	 * @return : Group of elements
	 */
	public Group getView()
	{
		VBox vbox = new VBox(this.title, this.form, this.footer);
		vbox.getStyleClass().add("head-title-container");
		return new Group(vbox);
	}
}
