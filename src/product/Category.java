package product;

public class Category {
	int id;
	String label;
	
	public Category()
	{}
	
	public Category(String label)
	{
		this.label = label;
	}
	
	
	public Category(int id, String label)
	{
		this.id = id;
		this.label = label;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getLabel()
	{
		return this.label;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String toString()
	{
		return this.label;
	}
}
