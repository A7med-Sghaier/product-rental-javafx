package client;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Class Client 
 */
public class Client {
	private SimpleIntegerProperty id;
	private SimpleStringProperty firstname;
	private SimpleStringProperty lastname;
	private SimpleStringProperty address;
	private SimpleStringProperty plz;
	private SimpleStringProperty city;
	private SimpleStringProperty tel;
	
	/**
	 * Constructor
	 */
	public Client() {
		this.id = new SimpleIntegerProperty();
		this.firstname = new SimpleStringProperty();
		this.lastname = new SimpleStringProperty();
		this.address = new SimpleStringProperty();
		this.plz = new SimpleStringProperty();
		this.city = new SimpleStringProperty();
		this.tel = new SimpleStringProperty();
	}
	
	/**
	 * Constructor
	 * @param id : client Id
	 * @param firstname : client name
	 * @param lastname : client last name
	 * @param address : client address
	 * @param plz : client plz
	 * @param city : client city
	 * @param tel : client telephone number
	 */
	public Client(int id, String firstname, String lastname, String address, String plz, String city, String tel)
	{
		this.id = new SimpleIntegerProperty(id);
		this.firstname = new SimpleStringProperty(firstname);
		this.lastname = new SimpleStringProperty(lastname);
		this.address = new SimpleStringProperty(address);
		this.plz = new SimpleStringProperty(plz);
		this.city = new SimpleStringProperty(city);
		this.tel = new SimpleStringProperty(tel);
	}

	/**
	 * @return client Id
	 */
	public int getId()
	{
		return this.id.get();
	}
	
	/**
	 * @param id : client Id
	 */
	public void setId(int id)
	{
		this.id.setValue(id);
	}
	
	public String getFirstname()
	{
		return this.firstname.get();
	}
	
	public void setFirstname(String firstname)
	{
		this.firstname.setValue(firstname);
	}
	
	public String getLastname()
	{
		return this.lastname.get();
	}
	
	public void setLastname(String lastname)
	{
		this.lastname.setValue(lastname);
	}
	
	public String getAddress()
	{
		return this.address.get();
	}
	
	public void setAddress(String address)
	{
		this.address.setValue(address);
	}
	
	public String getPlz()
	{
		return this.plz.get();
	}
	
	public void setPlz(String plz)
	{
		this.plz.setValue(plz);
	}
	
	public String getCity()
	{
		return this.city.get();
	}
	
	public void setCity(String city)
	{
		this.city.setValue(city);
	}
	
	public String getTel()
	{
		return this.tel.get();
	}
	
	
	public void setTel(String tel)
	{
		this.tel.setValue(tel);
	}
	
	/**
	 * Parse the client Object to a string for the DB Request (update or create)
	 * @return String
	 */
	public String parseClientToDB()
	{
		return 	"'" + this.firstname.get() + "' ,"+
				"'" + this.lastname.get() +  "' ,"+
				"'" + this.address.get() + "' ,"+
				"'" + this.plz.get() + "' ,"+
				"'" + this.city.get() +  "' ,"+
				"'" + this.tel.get() + "'";
	}
}
