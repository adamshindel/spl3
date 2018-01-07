package bgu.spl181.net.srv;

import java.util.ArrayList;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class User {

	public String userName;
	public String type;
	public String password;
	public String country;
	public Map<String, String> movies;
	public int balance;
	
	public User(String userName, String type, String password, String country, Map<String, String> movies, int balance) {
		this.userName = userName;
		this.type = type;
		this.password = password;
		this.country = country;
		this.movies = movies;
		this.balance = balance;
	}
	
}
