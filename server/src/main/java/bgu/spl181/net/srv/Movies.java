package bgu.spl181.net.srv;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Movies {
	
	private static final String location = "C:\\projects\\spl\\assignment 3\\project\\server\\Database\\Movies.json";
	private ArrayList <Movie> movies=new ArrayList <Movie>() ;
	
	public Movies(){
		JsonParser parser=new JsonParser();
		try {
			Object obj=parser.parse(new FileReader(location));
			JsonObject jsonObj=(JsonObject)obj;
			JsonArray jmovies=jsonObj.get("movies").getAsJsonArray();
			for(JsonElement element : jmovies) {
				String id =element.getAsJsonObject().get("id").getAsString();
				String name=element.getAsJsonObject().get("name").getAsString();
				int price =element.getAsJsonObject().get("price").getAsInt();
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

	public Movie getMovieByName(String name) {
		for (Movie m : movies) {
			if (m.name == name)
				return m;
		}
		return null;
	}

	
	public void addMovie(Movie toAdd) {
		try {

			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			FileReader fileReader = new FileReader(location);
			Object obj = parser.parse(fileReader);
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray jusers = jsonObj.get("users").getAsJsonArray();
			JsonElement newE = new JsonObject();
			JsonArray j = new JsonArray();
			for (String entry : toAdd.bannedCountries) {
				j.add(entry);
			}

			newE.getAsJsonObject().addProperty("id", toAdd.id);//don't forget increment
			newE.getAsJsonObject().addProperty("name", toAdd.name);
			newE.getAsJsonObject().addProperty("price", toAdd.price);
			newE.getAsJsonObject().add("bannedCountries", j);
			newE.getAsJsonObject().addProperty("availableAmount", toAdd.availableAmount);
			newE.getAsJsonObject().addProperty("totalAmount", toAdd.totalAmount);
			jusers.add(newE);
			FileWriter fileWriter = new FileWriter(location, false);
			fileWriter.write(gson.toJson(obj));
			fileWriter.flush();
			fileWriter.close();
			movies.add(toAdd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeMovie(Movie changedMovie, String property, String value) {

		try {

			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			FileReader fileReader = new FileReader(location);
			Object obj = parser.parse(fileReader);
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray jusers = jsonObj.get("users").getAsJsonArray();
			for (JsonElement element : jusers) {
				if(element.getAsJsonObject().get("username").getAsString().equals(changedMovie.name)) {
					element.getAsJsonObject().addProperty(property, value);
					break;
				}
			}
			FileWriter fileWriter = new FileWriter(location, false);
			fileWriter.write(gson.toJson(obj));
			fileWriter.close();
			
			for(Movie m : movies) {
				if(m.name == changedMovie.name) {
					movies.remove(m);
					movies.add(changedMovie);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
