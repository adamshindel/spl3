package bgu.spl181.net.srv;


import java.util.Map;



public class User {

	private String userName;
	private String type;
	private String password;
	private String country;
	private Map<String, String> movies;
	private String balance;
	
	public User(String userName, String type, String password, String country, Map<String, String> movies, String balance) {
		this.userName = userName;
		this.type = type;
		this.password = password;
		this.country = country;
		this.movies = movies;
		this.balance = balance;
	}
	public String getUserName() {
		return this.userName;
	}
	public String getType() {
		return this.type;
	}
	public String getPassword() {
		return this.password;
	}
	public String getCountry() {
		return this.country;
	}
	public Map<String, String> getMovies() {
		return this.movies;
	}
	public String getBalance() {
		return this.balance;
	}
	public void setBalance(String other){
		this.balance=other;
	}
	public void setMoviesy(Map<String, String> other) {
		this.movies=other;
	}
	
	
}
