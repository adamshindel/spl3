package bgu.spl181.net.srv;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SharedData extends SimpleSharedData {

	private static SharedData instance;
	private ReadWriteLock lock = new ReentrantReadWriteLock();

	// Users fields:
	private static final String locationUsers = "C:\\projects\\spl\\assignment 3\\project\\server\\Database\\Users.json";
	private ConcurrentLinkedQueue<User> users;
	private Map<Integer, User> loggedUsers;

	// Movies fields:
	private static final String locationMovies = "C:\\projects\\spl\\assignment 3\\project\\server\\Database\\Movies.json";
	private ConcurrentLinkedQueue<Movie> movies;
	private String highyestId = "0";

	private SharedData() {

		loadUsers();
		loadMovies();
	}

	public static synchronized SharedData getData() {
		if (instance == null) {
			instance = new SharedData();
		}
		return instance;
	}

	// Users :

	private void loadUsers() {
		this.users = new ConcurrentLinkedQueue<>();
		JsonParser parser = new JsonParser();
		try {
			FileReader fileReader = new FileReader(locationUsers);
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
			if (u.getUserName().compareTo(name) == 0)
				return u;
		}
		return null;
	}

	public User getLoggedByName(String name) {
		for (Entry<Integer, User> entry : loggedUsers.entrySet()) {
			if (entry.getValue().getUserName().compareTo(name) == 0)
				return entry.getValue();
		}
		return null;
	}

	public void addLoggedUser(User toAdd, Integer connectionId) {
		super.addLoggedUser(toAdd, connectionId);
		loggedUsers.put(connectionId, toAdd);
	}

	public User getLoggedUser(Integer connectionId) {
		return loggedUsers.get(connectionId);
	}

	public void addUser(User toAdd) {
		super.addUser(toAdd);
		try {
			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			lock.readLock().lock();
			FileReader fileReader = new FileReader(locationUsers);
			Object obj = parser.parse(fileReader);
			lock.readLock().unlock();
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray jusers = jsonObj.get("users").getAsJsonArray();
			JsonElement newE = new JsonObject();
			JsonArray j = new JsonArray();
			for (Map.Entry<String, String> entry : toAdd.getMovies().entrySet()) {
				JsonObject tmp = new JsonObject();
				tmp.addProperty("id", entry.getKey());
				tmp.addProperty("name", entry.getValue());
				j.add(tmp);
			}

			newE.getAsJsonObject().addProperty("username", toAdd.getUserName());
			newE.getAsJsonObject().addProperty("type", toAdd.getType());
			newE.getAsJsonObject().addProperty("password", toAdd.getPassword());
			newE.getAsJsonObject().addProperty("country", toAdd.getCountry());
			newE.getAsJsonObject().add("movies", j);
			newE.getAsJsonObject().addProperty("balance", toAdd.getBalance());
			jusers.add(newE);
			lock.writeLock().lock();
			FileWriter fileWriter = new FileWriter(locationUsers, false);
			fileWriter.write(gson.toJson(obj));
			fileWriter.flush();
			fileWriter.close();
			lock.writeLock().unlock();
			users.add(toAdd);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnectUser(User u) {
		super.disconnectUser(u);
		loggedUsers.remove(u);
	}

	public void changeUser(User changedUser, String property, String value) {

		try {

			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			lock.readLock().lock();
			FileReader fileReader = new FileReader(locationUsers);
			Object obj = parser.parse(fileReader);
			lock.readLock().unlock();
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray jusers = jsonObj.get("users").getAsJsonArray();
			for (JsonElement element : jusers) {
				if (element.getAsJsonObject().get("username").getAsString().equals(changedUser.getUserName())) {
					element.getAsJsonObject().addProperty(property, value);
					break;
				}
			}
			lock.writeLock().lock();
			FileWriter fileWriter = new FileWriter(locationUsers, false);
			fileWriter.write(gson.toJson(obj));
			fileWriter.close();
			lock.writeLock().unlock();
			for (User u : users) {
				if (u.getUserName().compareTo(changedUser.getUserName()) == 0) {
					users.remove(u);
					users.add(changedUser);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeMoviesUser(User changedUser) {

		try {

			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			lock.readLock().lock();
			FileReader fileReader = new FileReader(locationUsers);
			Object obj = parser.parse(fileReader);
			lock.readLock().unlock();
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray j = new JsonArray();
			for (Map.Entry<String, String> entry : changedUser.getMovies().entrySet()) {
				JsonObject tmp = new JsonObject();
				tmp.addProperty("id", entry.getKey());
				tmp.addProperty("name", entry.getValue());
				j.add(tmp);
			}
			JsonArray jusers = jsonObj.get("users").getAsJsonArray();

			for (JsonElement element : jusers) {
				if (element.getAsJsonObject().get("username").getAsString().equals(changedUser.getUserName())) {
					element.getAsJsonObject().add("movies", j);
					break;
				}
			}
			lock.writeLock().lock();
			FileWriter fileWriter = new FileWriter(locationUsers, false);
			fileWriter.write(gson.toJson(obj));
			fileWriter.close();
			lock.writeLock().unlock();
			for (User u : users) {
				if (u.getUserName().compareTo(changedUser.getUserName()) == 0) {
					users.remove(u);
					users.add(changedUser);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ConcurrentLinkedQueue<User> getUsers() {
		return users;
	}

	// Movies :

	private void loadMovies() {
		this.movies = new ConcurrentLinkedQueue<>();
		JsonParser parser = new JsonParser();
		try {
			Object obj = parser.parse(new FileReader(locationMovies));
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray jmovies = jsonObj.get("movies").getAsJsonArray();
			for (JsonElement element : jmovies) {
				String id = element.getAsJsonObject().get("id").getAsString();
				String name = element.getAsJsonObject().get("name").getAsString();
				String price = element.getAsJsonObject().get("price").getAsString();
				String availableAmount = element.getAsJsonObject().get("availableAmount").getAsString();
				String totalAmount = element.getAsJsonObject().get("totalAmount").getAsString();
				JsonArray jbannedCountries = element.getAsJsonObject().get("bannedCountries").getAsJsonArray();
				ArrayList<String> bannedCountries = new ArrayList<String>();
				for (JsonElement element2 : jbannedCountries) {
					bannedCountries.add(element2.getAsString());
				}
				movies.add(new Movie(id, name, price, availableAmount, totalAmount, bannedCountries));
				if (Integer.parseInt(id) > Integer.parseInt(highyestId)) {
					highyestId = id;
				}
			}

		} catch (Exception e) {

		}
	}

	public Movie getMovieByName(String name) {
		for (Movie m : movies) {
			if (m.getName().compareTo(name) == 0)
				return m;
		}
		return null;
	}

	public void addMovie(Movie toAdd) {
		movies.add(toAdd);
		if (Integer.parseInt(toAdd.getId()) > Integer.parseInt(highyestId)) {
			highyestId = toAdd.getId();
		}
		try {
			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			lock.readLock().lock();
			FileReader fileReader = new FileReader(locationMovies);
			Object obj = parser.parse(fileReader);
			lock.readLock().unlock();
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray jmovies = jsonObj.get("movies").getAsJsonArray();
			JsonElement newE = new JsonObject();
			JsonArray j = new JsonArray();
			for (String entry : toAdd.getBannedCountries()) {
				j.add(entry);
			}

			newE.getAsJsonObject().addProperty("id", toAdd.getId());// don't forget increment
			newE.getAsJsonObject().addProperty("name", toAdd.getName());
			newE.getAsJsonObject().addProperty("price", toAdd.getPrice());
			newE.getAsJsonObject().add("bannedCountries", j);
			newE.getAsJsonObject().addProperty("availableAmount", toAdd.getAviailableAmount());
			newE.getAsJsonObject().addProperty("totalAmount", toAdd.getTotalAmount());
			jmovies.add(newE);
			lock.writeLock().lock();
			FileWriter fileWriter = new FileWriter(locationMovies, false);
			fileWriter.write(gson.toJson(obj));
			fileWriter.flush();
			fileWriter.close();
			lock.writeLock().unlock();
			movies.add(toAdd);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void changeMovie(Movie changedMovie, String property, String value) {

		try {

			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			lock.readLock().lock();
			FileReader fileReader = new FileReader(locationMovies);
			Object obj = parser.parse(fileReader);
			lock.readLock().unlock();
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray jusers = jsonObj.get("movies").getAsJsonArray();
			for (JsonElement element : jusers) {
				if (element.getAsJsonObject().get("name").getAsString().equals(changedMovie.getName())) {
					element.getAsJsonObject().addProperty(property, value);
					break;
				}
			}
			lock.writeLock().lock();
			FileWriter fileWriter = new FileWriter(locationMovies, false);
			fileWriter.write(gson.toJson(obj));
			fileWriter.close();
			lock.writeLock().unlock();
			for (Movie m : movies) {
				if (m.getName().compareTo(changedMovie.getName()) == 0) {
					movies.remove(m);
					movies.add(changedMovie);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeMovie(Movie toRemove) {
		movies.remove(toRemove);
		if (toRemove.getId() == highyestId) {
			highyestId = (Integer.parseInt(highyestId) + 1) + "";
		}

		try {

			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			lock.readLock().lock();
			FileReader fileReader = new FileReader(locationMovies);
			Object obj = parser.parse(fileReader);
			lock.readLock().unlock();
			JsonObject jsonObj = (JsonObject) obj;
			JsonArray jusers = jsonObj.get("movies").getAsJsonArray();
			for (JsonElement element : jusers) {
				if (element.getAsJsonObject().get("name").getAsString().equals(toRemove.getName())) {
					jusers.remove(element);
					break;
				}
			}
			lock.writeLock().lock();
			FileWriter fileWriter = new FileWriter(locationMovies, false);
			fileWriter.write(gson.toJson(obj));
			fileWriter.close();
			lock.writeLock().unlock();
		} catch (Exception e) {

		}

	}

	public ConcurrentLinkedQueue<Movie> getMovies() {
		return movies;
	}

	public void setHighestId(String other) {
		this.highyestId = other;
	}

	public String getHighestId() {
		return highyestId;

	}
}
