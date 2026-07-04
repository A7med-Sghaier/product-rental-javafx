package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import product.Category;
import product.ProductDetails;
import product.Rent;


interface Callback{
	public void queryCallback() throws SQLException;
}

/**
 * Class DB for the Database  
 */
public class DB {
	
	Connection connection;
	Statement statement;
	
	/**
	 * Initialize the Database
	 */
	public DB()
	{
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Prepare the DB Statement to send a query to DB
	 * @param callback : a function that will be executed after DB connection
	 * @throws SQLException
	 * @throws Exception
	 */
	private void prepareStatement(Callback callback) throws SQLException, Exception
	{
		this.connection = null;
		try{
			this.connection = DriverManager.getConnection("jdbc:sqlite:Laiheus.db");
			this.statement = connection.createStatement();
			this.statement.setQueryTimeout(30);
			callback.queryCallback();
			
		} catch(SQLException e){
			System.err.println(e.getMessage());
		} finally {
			try
		      {
		        if(this.connection != null)
		        	this.connection.close();
		      }
		      catch(SQLException e)
		      {
		        System.err.println(e);
		      }
		}
	}
	
	/**
	 * Initialize the DB by creating tables if there not exists
	 * @throws Exception
	 */
	public void initDB() throws Exception
	{
		 
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				//statement.executeUpdate("DROP TABLE IF EXISTS clients");
				//statement.executeUpdate("DROP TABLE IF EXISTS products");
				//statement.executeUpdate("DROP TABLE IF EXISTS categories");
				//statement.executeUpdate("DROP TABLE IF EXISTS rents");
				
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS clients (id integer PRIMARY KEY, firstname string, lastname string, address string, plz string, city string, tel string)");
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS products (id integer PRIMARY KEY, label string, preis numeric, categorie_id integer, FOREIGN KEY (categorie_id) REFERENCES categories (id) ON DELETE CASCADE ON UPDATE NO ACTION)");
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS categories (id integer PRIMARY KEY, label string UNIQUE)");
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS rents (id integer PRIMARY KEY, c_id integer, p_id integer, status string, date_from date, date_to date, FOREIGN KEY (c_id) REFERENCES clients (id) ON DELETE CASCADE ON UPDATE NO ACTION, FOREIGN KEY (p_id) REFERENCES products (id) ON DELETE CASCADE ON UPDATE NO ACTION)");
				statement.executeUpdate("INSERT OR IGNORE INTO categories (label)  VALUES ('Technik'), ('Beauty & Drogerie'), ('Elektronik & Computer'), ('Sport & Freizeit');");
			};
		});
	}

	/**
	 * Get clients list from DB
	 * @param filter : contain the filter conditions to get the list of clients 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList<Client> getClients(Pair<String, String> filter) throws SQLException, Exception
	{
		ArrayList<Client> clientsList = new ArrayList<Client>();
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				ResultSet clients;
				if(filter != null && "filter".equals(filter.getKey()) && "hasRents".equals(filter.getValue())) {
					 clients =  statement.executeQuery("select * from clients c, rents r Where c.id = r.c_id AND r.status LIKE 'ausgeliehen' GROUP BY c.id");
				} else {
					 clients =  statement.executeQuery("select * from clients");
				}
				/* Prepare the clients list */
				while(clients.next()) {
					Client client = new Client();
					client.setId(clients.getInt("id"));
					client.setFirstname(clients.getString("firstname"));
					client.setLastname(clients.getString("lastname"));
					client.setAddress(clients.getString("address"));
					client.setPlz(clients.getString("plz"));
					client.setCity(clients.getString("city"));
					client.setTel(clients.getString("tel"));
					
					clientsList.add(client);
				}
			};
		});
		
		return clientsList;
	}
	
	/**
	 * Get a client by Id from DB 
	 * @param clientId : client Id
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Client getClientById(int clientId) throws SQLException, Exception
	{
		Client client = new Client();
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				ResultSet clientDB  =  statement.executeQuery("select * from clients c Where c.id = " + clientId);
				client.setId(clientDB.getInt("id"));
				client.setFirstname(clientDB.getString("firstname"));
				client.setLastname(clientDB.getString("lastname"));
				client.setAddress(clientDB.getString("address"));
				client.setPlz(clientDB.getString("plz"));
				client.setCity(clientDB.getString("city"));
				client.setTel(clientDB.getString("tel"));
			}
		});
		
		return client;
	}
	
	/**
	 * Add a client to DB
	 * @param client : client Object
	 * @throws SQLException
	 * @throws Exception
	 */
	public void addClient(Client client) throws SQLException, Exception {
		String values = client.parseClientToDB(); 
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				statement.executeUpdate("INSERT INTO clients (firstname, lastname, address, plz, city, tel) VALUES ("+ values +")");
			};
		});
	}

	/**
	 * Update a given client into the DB
	 * @param client : client Object
	 * @throws SQLException
	 * @throws Exception
	 */
	public void updateClient(Client client) throws SQLException, Exception {
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				statement.executeUpdate("UPDATE clients SET firstname = '"+ client.getFirstname() +
						"', lastname = '"+ client.getLastname() +
						"', address = '"+ client.getAddress()+
						"', plz = '"+ client.getPlz() + 
						"', city = '"+ client.getCity() +
						"', tel = '"+ client.getTel() +"' WHERE id = "+ client.getId() +";");
			};
		});
	}
	
	/**
	 * Remove a given client from the DB
	 * @param client : client Object
	 * @throws SQLException
	 * @throws Exception
	 */
	public void removeClient(Client client) throws SQLException, Exception {
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				statement.executeUpdate("DELETE FROM clients WHERE id = "+ client.getId() +";");
			};
		});
	}
	
	/******************************************************************************/
	
	
	/****************** Category Queries *****************/
	
	/**
	 * Get the categories list from DB
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList<Category> getCategories() throws SQLException, Exception
	{
		ArrayList<Category> categoriesList = new ArrayList<Category>();

		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				ResultSet categories = statement.executeQuery("select * from categories");
				while(categories.next()) {
					Category category = new Category();
					category.setId(categories.getInt("id"));
					category.setLabel(categories.getString("label"));
					categoriesList.add(category);
				}
			};
		});
		return categoriesList;
	}
	
	/**
	 * Add a category to DB
	 * @param category :  
	 * @throws SQLException
	 * @throws Exception
	 */
	public void addCategory(Category category) throws SQLException, Exception {
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				statement.executeUpdate("INSERT OR IGNORE INTO categories (label) VALUES ('"+ category.getLabel() +"')");
			};
		});
	}
	
	/**
	 * Update a given category into the DB
	 * @param category : category Object
	 * @throws SQLException
	 * @throws Exception
	 */
	public void updateCategory(Category category) throws SQLException, Exception {
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				statement.executeUpdate("UPDATE categories SET label = '"+ category.getLabel() +"' WHERE id = "+ category.getId() +";");
			};
		});
	}

	/**
	 * Remove a given category from the DB
	 * @param category : category Object
	 * @throws SQLException
	 * @throws Exception
	 */
	public void removeCategory(Category category) throws SQLException, Exception {
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				statement.executeUpdate("DELETE FROM categories WHERE id = "+ category.getId() +";");
			};
		});
	}
	/******************************************************************************/
	
	/****************** Product Queries *****************/
	
	/**
	 * Get a products list from DB
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList<ProductDetails> getProductOverView() throws SQLException, Exception
	{
		ArrayList<ProductDetails> productsList = new ArrayList<ProductDetails>();

		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				ResultSet productDetails = statement.executeQuery("SELECT p.id As pId,p.label AS pLabel,preis,categorie_id, c.label AS cLabel, r.id AS rId, c_id, p_id, CASE WHEN status IS NULL THEN 'verfügbar' ELSE status END AS status, date_from, date_to  FROM products p INNER JOIN categories c ON p.categorie_id = c.id LEFT JOIN rents r ON p.id = r.p_id AND r.status NOT LIKE 'returned'");
				while(productDetails.next()) {
					
					ProductDetails product = new ProductDetails();
					product.setProductId(productDetails.getInt("pId"));
					product.setProductname(productDetails.getString("pLabel"));
					product.setPreis(productDetails.getFloat("preis"));
					product.setCategory(new Category(productDetails.getInt("categorie_id"), productDetails.getString("cLabel")));
					
					product.setRentId(productDetails.getInt("rId"));
					product.setCid(productDetails.getInt("c_id"));
					product.setStatus(productDetails.getString("status"));
					product.setDatefrom(productDetails.getString("date_from"));
					product.setDateto(productDetails.getString("date_to"));
					productsList.add(product);
				}
			};
		});
		return productsList;
	}
	
	/**
	 * Add a product to DB
	 * @param product : product Object
	 * @throws SQLException
	 * @throws Exception
	 */
	public void addProduct(ProductDetails product) throws SQLException, Exception {
		String values = product.parseProduktToDb(); 
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				statement.executeUpdate("INSERT INTO products (label, categorie_id, preis) values ("+ values +")");
			};
		});
	}
	
	/**
	 * Update a given product into the DB
	 * @param product : product Object
	 * @throws SQLException
	 * @throws Exception
	 */
	public void updateProduct(ProductDetails product) throws SQLException, Exception {
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				statement.executeUpdate("UPDATE products SET label = '"+ product.getProductname() +"', "+
						"preis = " + product.getPreis() + "," +
						"categorie_id = "+ product.getCategory().getId()+ 
						" WHERE id = "+ product.getProductId() +";");
			};
		});
	}

	/**
	 * Remove a product from the DB
	 * @param product : product Object
	 * @throws SQLException
	 * @throws Exception
	 */
	public void removeProduct(ProductDetails product) throws SQLException, Exception 
	{
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				statement.executeUpdate("DELETE FROM products WHERE id = "+ product.getProductId() +";");
			};
		});
	}
	
	/******************************************************************************/

	/****************** Rent Queries *****************/
	
	/**
	 * Save a rent into the DB
	 * @param client : Client Object
	 * @param productList : product Object
	 * @throws SQLException
	 * @throws Exception
	 */
	public void saveRent(Client client, ObservableList<ProductDetails> productList) throws SQLException, Exception
	{
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				productList.forEach(product -> {
					product.setCid(client.getId());
					product.setStatus("ausgeliehen");
					String parsedRent = product.parseRentToDb();
					System.out.println(parsedRent);
					try {
						statement.executeUpdate("INSERT INTO rents (c_id, p_id, status, date_from, date_to) values ("+ parsedRent +")");
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});
			};
		});
	}
	
	/******************************************************************************/

	/****************** Return Queries *****************/
	/**
	 * Select the rents list of products for a given client from DB
	 * @param client : client Object
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList<ProductDetails> getRentedProductsByClient(Client client) throws SQLException, Exception
	{
		ArrayList<ProductDetails> rentedProducts = new ArrayList<ProductDetails>();
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				ResultSet rents = statement.executeQuery("select * from rents r LEFT JOIN products p ON r.p_id = p.id WHERE c_id = "+ client.getId() + " AND status LIKE 'ausgeliehen'");
				while(rents.next()) {
					ProductDetails rent = new ProductDetails();
					rent.setRentId(rents.getInt("id"));
					rent.setCid(rents.getInt("c_id"));
					rent.setProductId(rents.getInt("p_id"));
					rent.setPreis(rents.getFloat("preis"));
					rent.setStatus(rents.getString("status"));
					rent.setProductname(rents.getString("label"));
					rent.setDatefrom(rents.getString("date_from"));
					rent.setDateto(rents.getString("date_to"));
					rent.computePeriode();
					rentedProducts.add(rent);
				}
			};
		});
		
		return rentedProducts;
	}
	
	/**
	 * Get the rents list from DB
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList<Rent> getRentList() throws SQLException, Exception
	{
		ArrayList<Rent> rentList = new ArrayList<Rent>();
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				ResultSet rents = statement.executeQuery("select * from rents r LEFT JOIN products p ON r.p_id = p.id LEFT JOIN clients c ON r.c_id = c.id WHERE r.status LIKE 'ausgeliehen'");
				while(rents.next()) {
					Rent rent = new Rent();
					rent.setRentId(rents.getInt("id"));
					rent.setCid(rents.getInt("c_id"));
					rent.setProductId(rents.getInt("p_id"));
					rent.setPreis(rents.getFloat("preis"));
					rent.setStatus(rents.getString("status"));
					rent.setClientname(rents.getString("firstname") + ", " + rents.getString("lastname"));
					rent.setProductname(rents.getString("label"));
					rent.setDatefrom(rents.getString("date_from"));
					rent.setDateto(rents.getString("date_to"));
					rent.computePeriode();
					rentList.add(rent);
				}
			};
		});
		
		return rentList;
	}
	
	/******************************************************************************/

	/****************** Return Queries  *****************/
	
	/**
	 * Update a rent by setting its status to returned into the DB
	 * @param rentId : rent Id
	 * @param attribute : contain the key and value of the column to update
	 * @throws SQLException
	 * @throws Exception
	 */
	public void updateRent(int rentId, Pair<String, String> attribute) throws SQLException, Exception
	{
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback() throws SQLException {
				statement.executeUpdate("UPDATE rents SET "+ attribute.getKey() +" = '"+ attribute.getValue() +"' WHERE id = "+ rentId +";");
			};
		});
	}
	
	
	/******************************************************************************/

}

