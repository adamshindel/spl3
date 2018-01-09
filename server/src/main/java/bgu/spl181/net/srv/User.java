package bgu.spl181.net.srv;


import java.util.Map;



public class User extends SimpleUser{

	private String type;
	private String country;
	private Map<String, String> movies;
	private String balance;
	
	public User(String userName, String type, String password, String country, Map<String, String> movies, String balance) {
		super(userName,password);
		this.type = type;
		this.country = country;
		this.movies = movies;
		this.balance = balance;
	}
	public String getType() {
		return this.type;
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
