package product;

public class Status {

	int id;
	String label;
	
	public Status()
	{}
	
	public Status(int id, String label)
	{
		this.id = id;
		this.label = label;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public String getLabel()
	{
		return this.label;
	}
	
	public String toString()
	{
		return this.label;
	}
	
}
