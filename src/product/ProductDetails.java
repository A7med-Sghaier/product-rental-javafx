package product;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ProductDetails {

	protected SimpleStringProperty productName;
	protected SimpleStringProperty clientName;
	protected SimpleObjectProperty<Category> productCategory;
	protected SimpleFloatProperty preis;
	protected SimpleIntegerProperty rentId;
	protected SimpleIntegerProperty cId;
	protected SimpleIntegerProperty pId;
	protected SimpleStringProperty status;
	protected SimpleStringProperty dateFrom;
	protected SimpleStringProperty dateTo;
	protected SimpleIntegerProperty priode;
	
	public ProductDetails() {
		this.productName = new SimpleStringProperty();
		this.clientName = new SimpleStringProperty();
		this.productCategory = new SimpleObjectProperty<Category>();
		this.preis = new SimpleFloatProperty();
		this.cId = new SimpleIntegerProperty();
		this.pId = new SimpleIntegerProperty();
		this.rentId = new SimpleIntegerProperty();
		this.status = new SimpleStringProperty();
		this.dateFrom = new SimpleStringProperty();
		this.dateTo = new SimpleStringProperty();
		this.priode = new SimpleIntegerProperty();
	}
	
	public String getProductname()
	{
		return this.productName.get();
	}
	
	public void setProductname(String name)
	{
		this.productName.setValue(name);
	}
	
	
	public Category getCategory() {
		return this.productCategory.get();
	}
	
	public String getCategorylabel() {
		return this.productCategory.get().getLabel();
	}
	
	public void setCategory(Category category) {
		this.productCategory.setValue(category);
	}
	
	public Float getPreis() {
		return this.preis.get();
	}
	
	public void setPreis(Float preis) {
		this.preis.setValue(preis);
	}
	
	
	public int getCid()
	{
		return this.cId.get();
	}
	
	public void setCid(int c_id)
	{
		this.cId.setValue(c_id);
	}
	
	public int getProductId()
	{
		return this.pId.get();
	}
	
	public void setProductId(int p_id)
	{
		this.pId.setValue(p_id);
	}
	
	public int getRentId()
	{
		return this.rentId.get();
	}
	
	public void setRentId(int p_id)
	{
		this.rentId.setValue(p_id);
	}
	
	
	public String getStatus() {
		return this.status.get();
	}
	
	public void setStatus(String status) {
		this.status.setValue(status);
	}
	
	public String getDatefrom() {
		return this.dateFrom.get();
	}
	
	public void setDatefrom(String dateFrom) {
		this.dateFrom.setValue(dateFrom);
	}
	
	public String getDateto() {
		return this.dateTo.get();
	}
	
	public void setDateto(String dateTo) {
		this.dateTo.setValue(dateTo);
	}
	
	public int getPeriode() {
		return this.priode.get();
	}
	
	public void setPeriode(int periode) {
		this.priode.setValue(periode);
	}
	
	public void computePeriode()
	{
		LocalDate fromDate =  LocalDate.parse(this.getDatefrom());
		LocalDate fromTo = LocalDate.parse(this.getDateto());
		int dateDiff = (int) ChronoUnit.DAYS.between(fromDate, fromTo);
		this.priode.setValue(dateDiff);
	}
	
	public float calculateTotal()
	{
		return this.getPeriode() * this.getPreis();
	}
	
	public String parseProduktToDb()
	{
		return 	"'" + this.productName.get() + "' ,"+
				this.productCategory.get().getId() + " ,"+
				this.preis.get();
	}
	
	public String parseRentToDb()
	{
		return 	this.cId.get() + " ,"+
				this.pId.get() + " ,"+
				"'"+ this.status.get() + "',"+
				"'"+ this.dateFrom.get() + "',"+
				"'"+ this.dateTo.get() + "'";
	}
}
