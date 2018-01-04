package bgu.spl181.net.srv;

import java.io.FileReader;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Movies {
	private ArrayList <Movie> movies=new ArrayList <Movie>() ;
	public Movies(){
		JsonParser parser=new JsonParser();
		try {
			Object obj=parser.parse(new FileReader("C:\\projects\\spl\\assignment 3\\project\\spl-net\\example_Users.json"));
			JsonObject jsonObj=(JsonObject)obj;
			JsonArray jmovies=jsonObj.get("movies").getAsJsonArray();
			for(JsonElement element : jmovies) {
				String id =element.getAsJsonObject().get("id").getAsString();
				String name=element.getAsJsonObject().get("name").getAsString();
				String price =element.getAsJsonObject().get("price").getAsString();
				String availableAmount =element.getAsJsonObject().get("availableAmount").getAsString();
				String totalAmount =element.getAsJsonObject().get("totalAmount").getAsString();
				JsonArray jbannedCountries=element.getAsJsonObject().get("bannedCountries").getAsJsonArray();
				ArrayList <String> bannedCountries=new ArrayList<String>();
				for(JsonElement element2 : jbannedCountries){
					bannedCountries.add(element2.getAsString());
				}
				movies.add(new Movie(id,name,price,availableAmount,totalAmount,bannedCountries));  
			}
			
		}
		catch(Exception e) {
			
		}
		
	}
}
