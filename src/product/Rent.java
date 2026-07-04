package product;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleFloatProperty;


public class Rent extends ProductDetails{

	protected SimpleStringProperty clientName;
	
	public Rent()
	{
		super();
		this.clientName = new SimpleStringProperty();
	}
	
	public String getClientname()
	{
		return this.clientName.get();
	}
	
	public void setClientname(String name)
	{
		this.clientName.setValue(name);
	}
	
}
