package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

import client.Client;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import product.Category;
import product.ProductDetails;
import product.Rent;

interface Callback {
	public void queryCallback(Connection connection) throws SQLException;
}

/**
 * Class DB for the Database
 */
public class DB {

	private static final String DATABASE_URL = "jdbc:sqlite:Laiheus.db";
	private static final Set<String> RENT_UPDATE_COLUMNS = Set.of("status", "date_from", "date_to");

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
	 * Prepare the DB connection to send a query to DB
	 * @param callback : a function that will be executed after DB connection
	 * @throws SQLException
	 * @throws Exception
	 */
	private void prepareStatement(Callback callback) throws SQLException, Exception
	{
		try(Connection connection = DriverManager.getConnection(DATABASE_URL)) {
			callback.queryCallback(connection);
		} catch(SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	private PreparedStatement buildStatement(Connection connection, String sql, Object... params) throws SQLException
	{
		PreparedStatement statement = connection.prepareStatement(sql);
		for(int i = 0; i < params.length; i++) {
			statement.setObject(i + 1, params[i]);
		}
		return statement;
	}

	private int executeUpdate(Connection connection, String sql, Object... params) throws SQLException
	{
		try(PreparedStatement statement = this.buildStatement(connection, sql, params)) {
			return statement.executeUpdate();
		}
	}

	private ResultSet executeQuery(Connection connection, String sql, Object... params) throws SQLException
	{
		PreparedStatement statement = this.buildStatement(connection, sql, params);
		return statement.executeQuery();
	}

	private String getRentUpdateColumn(String column)
	{
		if(!RENT_UPDATE_COLUMNS.contains(column)) {
			throw new IllegalArgumentException("Unsupported rent update column: " + column);
		}
		return column;
	}

	/**
	 * Initialize the DB by creating tables if there not exists
	 * @throws Exception
	 */
	public void initDB() throws Exception
	{
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback(Connection connection) throws SQLException {
				executeUpdate(connection, "CREATE TABLE IF NOT EXISTS clients (id integer PRIMARY KEY, firstname string, lastname string, address string, plz string, city string, tel string)");
				executeUpdate(connection, "CREATE TABLE IF NOT EXISTS products (id integer PRIMARY KEY, label string, preis numeric, categorie_id integer, FOREIGN KEY (categorie_id) REFERENCES categories (id) ON DELETE CASCADE ON UPDATE NO ACTION)");
				executeUpdate(connection, "CREATE TABLE IF NOT EXISTS categories (id integer PRIMARY KEY, label string UNIQUE)");
				executeUpdate(connection, "CREATE TABLE IF NOT EXISTS rents (id integer PRIMARY KEY, c_id integer, p_id integer, status string, date_from date, date_to date, FOREIGN KEY (c_id) REFERENCES clients (id) ON DELETE CASCADE ON UPDATE NO ACTION, FOREIGN KEY (p_id) REFERENCES products (id) ON DELETE CASCADE ON UPDATE NO ACTION)");
				executeUpdate(connection, "INSERT OR IGNORE INTO categories (label)  VALUES ('Technik'), ('Beauty & Drogerie'), ('Elektronik & Computer'), ('Sport & Freizeit')");
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
			public void queryCallback(Connection connection) throws SQLException {
				ResultSet clients;
				if(filter != null && "filter".equals(filter.getKey()) && "hasRents".equals(filter.getValue())) {
					clients = executeQuery(connection, "select * from clients c, rents r Where c.id = r.c_id AND r.status LIKE ? GROUP BY c.id", "ausgeliehen");
				} else {
					clients = executeQuery(connection, "select * from clients");
				}
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
				clients.getStatement().close();
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
			public void queryCallback(Connection connection) throws SQLException {
				ResultSet clientDB = executeQuery(connection, "select * from clients c Where c.id = ?", clientId);
				if(clientDB.next()) {
					client.setId(clientDB.getInt("id"));
					client.setFirstname(clientDB.getString("firstname"));
					client.setLastname(clientDB.getString("lastname"));
					client.setAddress(clientDB.getString("address"));
					client.setPlz(clientDB.getString("plz"));
					client.setCity(clientDB.getString("city"));
					client.setTel(clientDB.getString("tel"));
				}
				clientDB.getStatement().close();
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
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback(Connection connection) throws SQLException {
				executeUpdate(connection,
					"INSERT INTO clients (firstname, lastname, address, plz, city, tel) VALUES (?, ?, ?, ?, ?, ?)",
					client.getFirstname(),
					client.getLastname(),
					client.getAddress(),
					client.getPlz(),
					client.getCity(),
					client.getTel()
				);
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
			public void queryCallback(Connection connection) throws SQLException {
				executeUpdate(connection,
					"UPDATE clients SET firstname = ?, lastname = ?, address = ?, plz = ?, city = ?, tel = ? WHERE id = ?",
					client.getFirstname(),
					client.getLastname(),
					client.getAddress(),
					client.getPlz(),
					client.getCity(),
					client.getTel(),
					client.getId()
				);
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
			public void queryCallback(Connection connection) throws SQLException {
				executeUpdate(connection, "DELETE FROM clients WHERE id = ?", client.getId());
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
			public void queryCallback(Connection connection) throws SQLException {
				ResultSet categories = executeQuery(connection, "select * from categories");
				while(categories.next()) {
					Category category = new Category();
					category.setId(categories.getInt("id"));
					category.setLabel(categories.getString("label"));
					categoriesList.add(category);
				}
				categories.getStatement().close();
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
			public void queryCallback(Connection connection) throws SQLException {
				executeUpdate(connection, "INSERT OR IGNORE INTO categories (label) VALUES (?)", category.getLabel());
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
			public void queryCallback(Connection connection) throws SQLException {
				executeUpdate(connection, "UPDATE categories SET label = ? WHERE id = ?", category.getLabel(), category.getId());
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
			public void queryCallback(Connection connection) throws SQLException {
				executeUpdate(connection, "DELETE FROM categories WHERE id = ?", category.getId());
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
			public void queryCallback(Connection connection) throws SQLException {
				ResultSet productDetails = executeQuery(connection, "SELECT p.id As pId,p.label AS pLabel,preis,categorie_id, c.label AS cLabel, r.id AS rId, c_id, p_id, CASE WHEN status IS NULL THEN 'verfügbar' ELSE status END AS status, date_from, date_to  FROM products p INNER JOIN categories c ON p.categorie_id = c.id LEFT JOIN rents r ON p.id = r.p_id AND r.status NOT LIKE 'returned'");
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
				productDetails.getStatement().close();
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
		this.prepareStatement(new Callback() {
			@Override
			public void queryCallback(Connection connection) throws SQLException {
				executeUpdate(connection,
					"INSERT INTO products (label, categorie_id, preis) values (?, ?, ?)",
					product.getProductname(),
					product.getCategory().getId(),
					product.getPreis()
				);
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
			public void queryCallback(Connection connection) throws SQLException {
				executeUpdate(connection,
					"UPDATE products SET label = ?, preis = ?, categorie_id = ? WHERE id = ?",
					product.getProductname(),
					product.getPreis(),
					product.getCategory().getId(),
					product.getProductId()
				);
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
			public void queryCallback(Connection connection) throws SQLException {
				executeUpdate(connection, "DELETE FROM products WHERE id = ?", product.getProductId());
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
			public void queryCallback(Connection connection) throws SQLException {
				for(ProductDetails product : productList) {
					product.setCid(client.getId());
					product.setStatus("ausgeliehen");
					executeUpdate(connection,
						"INSERT INTO rents (c_id, p_id, status, date_from, date_to) values (?, ?, ?, ?, ?)",
						product.getCid(),
						product.getProductId(),
						product.getStatus(),
						product.getDatefrom(),
						product.getDateto()
					);
				}
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
			public void queryCallback(Connection connection) throws SQLException {
				ResultSet rents = executeQuery(connection, "select * from rents r LEFT JOIN products p ON r.p_id = p.id WHERE c_id = ? AND status LIKE ?", client.getId(), "ausgeliehen");
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
				rents.getStatement().close();
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
			public void queryCallback(Connection connection) throws SQLException {
				ResultSet rents = executeQuery(connection, "select * from rents r LEFT JOIN products p ON r.p_id = p.id LEFT JOIN clients c ON r.c_id = c.id WHERE r.status LIKE ?", "ausgeliehen");
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
				rents.getStatement().close();
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
			public void queryCallback(Connection connection) throws SQLException {
				String column = getRentUpdateColumn(attribute.getKey());
				executeUpdate(connection, "UPDATE rents SET " + column + " = ? WHERE id = ?", attribute.getValue(), rentId);
			};
		});
	}

	/******************************************************************************/

}
