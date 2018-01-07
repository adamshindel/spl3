package bgu.spl181.net.srv;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class User {

	public String userName;
	public String type;
	public String password;
	public String country;
	public ArrayList<Movie> movie;
	public int balance;
	
	public User(String userName, String type, String password, String country, ArrayList<Movie> movie, int balance) {
		this.userName = userName;
		this.type = type;
		this.password = password;
		this.country = country;
		this.movie = movie;
		this.balance = balance;
	}
	
}
