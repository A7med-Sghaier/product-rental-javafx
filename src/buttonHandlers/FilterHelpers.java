package buttonHandlers;

import java.time.LocalDate;

import client.Client;
import gui.MainContainer;
import gui.UpdateClientView;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.util.Pair;
import product.ProductDetails;
import product.Rent;

/**
 * Class Helper for Filters  
 */
public class FilterHelpers {
	
	/* A boolean variable to indicate if the filter is valid or not*/
	public static boolean filterVal = false;
	
	/**
	 * Compute the filter for a search by client name 
	 * @param clientName : contain the client name of a given item from a list
	 * @param newVal : contain the client name given on the filter field
	 * @return : true or false
	 */
	public static boolean clientNameFilter(String clientName, String newVal)
	{
		if(newVal == null || newVal.isEmpty()) {
			return true;
		}
		
		if(clientName.toLowerCase().contains(newVal.toLowerCase())) {
				return true;
			}
		
		return false;
	}
	
	/**
	 * Compute the filter for a search by product name 
	 * @param productName : contain the product name of a given item from a list
	 * @param newVal : contain the product name given on the filter field
	 * @return : true or false
	 */
	public static boolean productNameFilter(String productName, String newVal)
	{
		if(newVal == null || newVal.isEmpty()) {
			return true;
		}
		
		if(productName.toLowerCase().contains(newVal.toLowerCase())) {
				return true;
			}
		return false;
	}
	
	/**
	 * Compute the filter for search by start date  
	 * @param dateFrom : contain the start date of a given item from a list
	 * @param newVal : contain the start date given on the filter field
	 * @return : true or false
	 */
	public static boolean DateFromFilter(String dateFrom, String newVal)
	{
		if(newVal == null || newVal.isEmpty()) {
			return true;
		}
		
		LocalDate date = LocalDate.parse(dateFrom);
		LocalDate newDate = LocalDate.parse(newVal);
		return newDate.compareTo(date) <= 0;
	}
	
	/**
	 * Compute the filter for a search by an end date  
	 * @param dateTo : contain the end date of a given item from a list
	 * @param newVal : contain the end date given on the filter field
	 * @return : true or false
	 */
	public static boolean DateToFilter(String dateTo, String newVal)
	{
		if(newVal == null || newVal.isEmpty()) {
			return true;
		}
		
		LocalDate date = LocalDate.parse(dateTo);
		LocalDate newDate = LocalDate.parse(newVal);
		return newDate.compareTo(date) >= 0;
	}
	
	/**
	 * Compute the filter for a search by category name 
	 * @param categoryLabel : contain the category name of a given item from a list
	 * @param newVal : contain the category name given on the filter field
	 * @return
	 */
	public static boolean categoryNameFilter(String categoryLabel, String newVal) {

		if(newVal == null || newVal.isEmpty()) {
			return true;
		}
		
		if(categoryLabel.toLowerCase().contains(newVal.toLowerCase())) {
				return true;
			}
		return false;
	}
	
	/**
	 * Compute the filter for a search by a category  
	 * @param categoryId : contain the category Id of a given item from a list
	 * @param newVal : contain the category Id given on the filter field
	 * @return : true or false
	 */
	public static boolean CategoryFilter(int categoryId, int newVal)
	{
		if(newVal== -1 || newVal == categoryId) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Compute the filter by searching for an end date  
	 * @param statusLabel : contain the category Id of a given item from a list
	 * @param newVal : contain the category Id given on the filter field
	 * @return : true or false
	 */
	public static boolean StatusFilter(String statusLabel, String newVal)
	{
		if(newVal == null || newVal.isEmpty()) {
			return true;
		}
		
		if(statusLabel.toLowerCase().contains(newVal.toLowerCase())) {
				return true;
			}
		return false;
	}
	
	/**
	 * Compute the combined filter applied to the rented products list for a search by many items  
	 * @param rent : is a Rent
	 * @param observableList : is the List of rented products
	 * @return : true or false
	 */
	public static boolean multiFilter(Rent rent, ObservableList<? extends Pair<String,String>> observableList)
	{
		FilterHelpers.filterVal = true;
		
		observableList.forEach(item->{
			switch(item.getKey()) {
				case "client":
					FilterHelpers.filterVal  &= FilterHelpers.clientNameFilter(rent.getClientname(), item.getValue());
					break;
				case "product":
					FilterHelpers.filterVal  &= FilterHelpers.productNameFilter(rent.getProductname(), item.getValue());
					break;
				case "dateFrom":
					FilterHelpers.filterVal  &= FilterHelpers.DateFromFilter(rent.getDatefrom(), item.getValue());
					break;
				case "dateTo":
					FilterHelpers.filterVal  &= FilterHelpers.DateToFilter(rent.getDateto(), item.getValue());
					break;
				case "status":
					FilterHelpers.filterVal  &= FilterHelpers.StatusFilter(rent.getStatus(), item.getValue());
					break;
			}
		});
		return FilterHelpers.filterVal;
	}
	
	/**
	 * Compute the combined filter applied to the products list for a search by many items  
	 * @param product : is a Product
	 * @param observableList : is the List of products
	 * @return : true or false
	 */
	public static boolean multiFilter(ProductDetails product, ObservableList<? extends Pair<String,String>> observableList)
	{
		FilterHelpers.filterVal = true;
		
		observableList.forEach(item->{
			switch(item.getKey()) {
				case "product":
					FilterHelpers.filterVal  &= FilterHelpers.productNameFilter(product.getProductname(), item.getValue());
					break;
				case "category":
					FilterHelpers.filterVal  &= FilterHelpers.CategoryFilter(product.getCategory().getId(), Integer.parseInt(item.getValue()));
					break;
				case "status":
					FilterHelpers.filterVal  &= FilterHelpers.StatusFilter(product.getStatus(), item.getValue());
					break;
			}
		});
		return FilterHelpers.filterVal;
	}

}
