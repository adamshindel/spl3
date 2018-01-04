package bgu.spl181.net.srv;

import java.io.FileReader;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Movie_Rental_Service_Users {
	
	public ArrayList<User> users;
	
	public Movie_Rental_Service_Users() {
		this.users = new ArrayList<>();
		JsonParser parser=new JsonParser();
		try {
			Object obj=parser.parse(new FileReader("C:\\projects\\spl\\assignment 3\\project\\spl-net\\example_Users.json"));
			JsonObject jsonObj=(JsonObject)obj;
			JsonArray jusers=jsonObj.get("users").getAsJsonArray();
			for(JsonElement element : jusers) {
				String userName =element.getAsJsonObject().get("username").getAsString();
				String type =element.getAsJsonObject().get("type").getAsString();
				String password =element.getAsJsonObject().get("password").getAsString();
				String country =element.getAsJsonObject().get("country").getAsString();
				JsonArray jmovies=jsonObj.get("movies").getAsJsonArray();
				ArrayList<Movie> movies = new ArrayList<Movie>();
				for(JsonElement jmovie : jmovies) {
						String id =jmovie.getAsJsonObject().get("id").getAsString();
						String name=jmovie.getAsJsonObject().get("name").getAsString();
						String price =jmovie.getAsJsonObject().get("price").getAsString();
						String availableAmount =jmovie.getAsJsonObject().get("availableAmount").getAsString();
						String totalAmount =jmovie.getAsJsonObject().get("totalAmount").getAsString();
						JsonArray jbannedCountries=jmovie.getAsJsonObject().get("bannedCountries").getAsJsonArray();
						ArrayList <String> bannedCountries=new ArrayList<String>();
						for(JsonElement element2 : jbannedCountries){
							bannedCountries.add(element2.getAsString());
						}
						movies.add(new Movie(id,name,price,availableAmount,totalAmount,bannedCountries));  
				}
				String balance =element.getAsJsonObject().get("balance").getAsString();
				this.users.add(new User(userName, type, password, country, movies, balance));
			}
			
		}
		catch(Exception e) {
			
		}
	}
	
	
}
