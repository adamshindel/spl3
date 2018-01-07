package bgu.spl181.net.srv;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.imageio.IIOException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Movie_Rental_Service_Users {

	private static final String location = "C:\\projects\\spl\\assignment 3\\project\\server\\Database\\Users.json";
	private ArrayList<User> users;
	private ArrayList<User> loggedUsers;

	public Movie_Rental_Service_Users() {

		this.users = new ArrayList<>();
		JsonParser parser = new JsonParser();
		try {
			FileReader fileReader = new FileReader(location);
			Object obj = parser.parse(fileReader);
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray jusers = jsonObj.get("users").getAsJsonArray();
			for (JsonElement element : jusers) {
				String userName = element.getAsJsonObject().get("username").getAsString();
				String type = element.getAsJsonObject().get("type").getAsString();
				String password = element.getAsJsonObject().get("password").getAsString();
				String country = element.getAsJsonObject().get("country").getAsString();
				JsonArray jmovies = element.getAsJsonObject().get("movies").getAsJsonArray();
				ArrayList<Movie> movies = new ArrayList<Movie>();
				for (JsonElement jmovie : jmovies) {
					String id = jmovie.getAsJsonObject().get("id").getAsString();
					String name = jmovie.getAsJsonObject().get("name").getAsString();
					int price = jmovie.getAsJsonObject().get("price").getAsInt();
					String availableAmount = jmovie.getAsJsonObject().get("availableAmount").getAsString();
					String totalAmount = jmovie.getAsJsonObject().get("totalAmount").getAsString();
					JsonArray jbannedCountries = jmovie.getAsJsonObject().get("bannedCountries").getAsJsonArray();
					ArrayList<String> bannedCountries = new ArrayList<String>();
					for (JsonElement element2 : jbannedCountries) {
						bannedCountries.add(element2.getAsString());
					}
					movies.add(new Movie(id, name, price, availableAmount, totalAmount, bannedCountries));
				}
				int balance = element.getAsJsonObject().get("balance").getAsInt();
				this.users.add(new User(userName, type, password, country, movies, balance));
			}
			fileReader.close();
		} catch (Exception e) {

		}
	}

	public User getUserByName(String name) {
		for (User u : users) {
			if (u.userName == name)
				return u;
		}
		return null;
	}

	public User getLoggedByName(String name) {
		for (User u : loggedUsers) {
			if (u.userName == name)
				return u;
		}
		return null;
	}

	public void addLoggedUser(User toAdd) {
		loggedUsers.add(toAdd);
	}

	public void addUser(User toAdd) {
		try {
			/*Gson gson = new Gson();
			String jsonString = gson.toJson(toAdd);
			FileWriter fileWriter = new FileWriter(location, true);
*/
			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			FileReader fileReader = new FileReader(location);
			Object obj = parser.parse(fileReader);
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray jusers = jsonObj.get("users").getAsJsonArray();
			jusers.add(gson.toJson(toAdd));

			//fileWriter.write(jsonString);
			//fileWriter.close();
			users.add(toAdd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnectUser(User u) {
		loggedUsers.remove(u);
	}

	public void changeBalanceUser(String userName, int balance) {
		JsonParser parser = new JsonParser();
		try {

			FileReader fileReader = new FileReader(location);
			Object obj = parser.parse(fileReader);
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray jusers = jsonObj.get("users").getAsJsonArray();
			for (JsonElement element : jusers) {
				if (element.getAsJsonObject().get("username").getAsString().equals(userName)) {
					// element.getAsJsonObject().addProperty("blance", balance);
					jsonObj.remove(userName);
					break;
				}
			}
		} catch (Exception e) {

		}
	}

	public ArrayList<User> getUsers() {
		return users;
	}

}
