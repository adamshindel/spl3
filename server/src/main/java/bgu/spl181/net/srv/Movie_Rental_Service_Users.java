package bgu.spl181.net.srv;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
				Map<String, String> movies = new ConcurrentHashMap<>();
				for (JsonElement jmovie : jmovies) {

					String id = jmovie.getAsJsonObject().get("id").getAsString();
					String name = jmovie.getAsJsonObject().get("name").getAsString();
					movies.put(id, name);
					/*
					 * int price = jmovie.getAsJsonObject().get("price").getAsInt(); String
					 * availableAmount =
					 * jmovie.getAsJsonObject().get("availableAmount").getAsString(); String
					 * totalAmount = jmovie.getAsJsonObject().get("totalAmount").getAsString();
					 * JsonArray jbannedCountries =
					 * jmovie.getAsJsonObject().get("bannedCountries").getAsJsonArray();
					 * ArrayList<String> bannedCountries = new ArrayList<String>(); for (JsonElement
					 * element2 : jbannedCountries) { bannedCountries.add(element2.getAsString()); }
					 * movies.add(new Movie(id, name, price, availableAmount, totalAmount,
					 * bannedCountries));
					 */
				}
				String balance = element.getAsJsonObject().get("balance").getAsString();
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

			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			FileReader fileReader = new FileReader(location);
			Object obj = parser.parse(fileReader);
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray jusers = jsonObj.get("users").getAsJsonArray();
			JsonElement newE = new JsonObject();
			JsonArray j = new JsonArray();
			for (Map.Entry<String, String> entry : toAdd.movies.entrySet()) {
				JsonObject tmp = new JsonObject();
				tmp.addProperty("id", entry.getKey());
				tmp.addProperty("name", entry.getValue());
				j.add(tmp);
			}

			newE.getAsJsonObject().addProperty("username", toAdd.userName);
			newE.getAsJsonObject().addProperty("type", toAdd.type);
			newE.getAsJsonObject().addProperty("password", toAdd.password);
			newE.getAsJsonObject().addProperty("country", toAdd.country);
			newE.getAsJsonObject().add("movies", j);
			newE.getAsJsonObject().addProperty("balance", toAdd.balance);
			jusers.add(newE);
			FileWriter fileWriter = new FileWriter(location, false);
			fileWriter.write(gson.toJson(obj));
			fileWriter.flush();
			fileWriter.close();
			users.add(toAdd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnectUser(User u) {
		loggedUsers.remove(u);
	}

	public void changeUser(User changedUser, String property, String value) {

		try {

			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			FileReader fileReader = new FileReader(location);
			Object obj = parser.parse(fileReader);
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray jusers = jsonObj.get("users").getAsJsonArray();
			for (JsonElement element : jusers) {
				if(element.getAsJsonObject().get("username").getAsString().equals(changedUser.userName)) {
					element.getAsJsonObject().addProperty(property, value);
					break;
				}
			}
			FileWriter fileWriter = new FileWriter(location, false);
			fileWriter.write(gson.toJson(obj));
			fileWriter.close();
			
			for(User u : users) {
				if(u.userName == changedUser.userName) {
					users.remove(u);
					users.add(changedUser);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void changeMoviesUser(User changedUser, Map<String,String> movies) {

		try {

			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			FileReader fileReader = new FileReader(location);
			Object obj = parser.parse(fileReader);
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray j = new JsonArray();
			for (Map.Entry<String, String> entry : movies.entrySet()) {
				JsonObject tmp = new JsonObject();
				tmp.addProperty("id", entry.getKey());
				tmp.addProperty("name", entry.getValue());
				j.add(tmp);
			}
			JsonArray jusers = jsonObj.get("users").getAsJsonArray();
			
			for (JsonElement element : jusers) {
				if(element.getAsJsonObject().get("username").getAsString().equals(changedUser.userName)) {
					element.getAsJsonObject().add("movies", j);
					break;
				}
			}
			FileWriter fileWriter = new FileWriter(location, false);
			fileWriter.write(gson.toJson(obj));
			fileWriter.close();
			
			for(User u : users) {
				if(u.userName == changedUser.userName) {
					users.remove(u);
					users.add(changedUser);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public ArrayList<User> getUsers() {
		return users;
	}

}
