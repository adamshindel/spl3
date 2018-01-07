package bgu.spl181.net.srv;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

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

	public static void addMovie(Movie toAdd) {
		try {
			Gson gson = new Gson();
			String jsonString = gson.toJson(toAdd);
			FileWriter fileWriter = new FileWriter(location, true);
			fileWriter.write(jsonString);
			fileWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
