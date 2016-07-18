import org.json.*;
import java.io.*;
import java.util.*;

public class Matching {
	
	public static class Product {
	    public String model;
	    public String productName;
	    public ArrayList<String> listings = new ArrayList<String>();
	    public Product(String m, String product_name){
	    	model = m;
	    	productName = product_name;
	    }
	    
	    public boolean matchModel(String title){
	    	if(title.toLowerCase().contains(" " + model + " "))
	    		return true;
	    	else
	    	    return false;
	    }
	    
	    public void addToListings(String listing){
	    	listings.add(listing);
	    }
	}
	
   public static void main(String[] args){
	   
	   String prodFileName = args[0];
	   String priceFileName = args[1];
	   String resultFileName = args[2];
     
	/* Store all the products into a hashmap, product manufacture is the key, 
	 * each manufacture contains a list of products with unique model and name
	 */
	   BufferedReader prodBr = null;
       String productLine = "";
    
       HashMap<String,ArrayList<Product>> AllProducts = new HashMap<String,ArrayList<Product>>();
       
       try {
    	   prodBr = new BufferedReader( new FileReader(prodFileName));
           while( (productLine = prodBr.readLine()) != null){
        	   JSONObject obj = new JSONObject(productLine);
        	   String manuFact = obj.getString("manufacturer").toLowerCase();
        	   String model = obj.getString("model").toLowerCase();
        	   String product_name = obj.getString("product_name");
        	  
        	   if(!AllProducts.containsKey(manuFact))
        	   {
        	       ArrayList<Product> modelList = new ArrayList<Product>();
        	       modelList.add(new Product(model,product_name));
        	       AllProducts.put(manuFact, modelList);
        	   }else{
        		   AllProducts.get(manuFact).add(new Product(model,product_name));
        	   }
           }
           prodBr.close();
       } catch (FileNotFoundException e) {
           System.err.println("Unable to find the products.txt file");
       } catch (IOException e) {
           System.err.println("Unable to read the products.txt file");
       }
       
       // read each listing and match it with product, first by manufacture (brand) then by model
       BufferedReader listBr = null;
       String priceLine = "";
       try {
    	   listBr = new BufferedReader( new FileReader(priceFileName));
           while( (priceLine = listBr.readLine()) != null){
        	   JSONObject priceJsonObj = new JSONObject(priceLine);
        	   String priceManufacturer = priceJsonObj.getString("manufacturer");
        	   String brand = "";
               for(String str: AllProducts.keySet())
               {
            	   if(priceManufacturer.toLowerCase().contains(str))
            	   {
            	       brand = str;
            	       break;
            	   }
               }
               if(brand.length() > 0)
               {
            	   String title = priceJsonObj.getString("title");
            	   ArrayList<Product> brandProducts = AllProducts.get(brand);
            	   for(int i = 0; i < brandProducts.size(); i++)
            	   {
            		   if(brandProducts.get(i).matchModel(title))
            		   {
            			   AllProducts.get(brand).get(i).addToListings(priceLine);
            			   break;
            		   }
            	   }
               }
           }
           listBr.close();
       } catch (FileNotFoundException e) {
           System.err.println("Unable to find the listings.txt file");
       } catch (IOException e) {
           System.err.println("Unable to read the listings.txt file");
       }
       
       // write result into result.txt file
       try{
    		FileWriter fw = new FileWriter(resultFileName);

    	       for(ArrayList<Product> pArr : AllProducts.values())
    	       {
    	    	   for(Product prod : pArr)
    	    	   {
    	    		   if(!prod.listings.isEmpty())
    	    		   {
    	    			   fw.write("{\"product_name\":" + prod.productName + ",");
    	    			   fw.write(" \"listings\":[ ");
    	        	       for(String l : prod.listings)
    	        	       {
    	        	    	   fw.write("listing:" + l +" ");
    	        	       }
    	        	       fw.write("]} \n");
    	    		   }
    	    	   }
    	       }
    		fw.close();
       }catch(IOException e){
    	   System.err.println("Unable to write to the result.txt file");
       }
       
   }
 }

