package bgu.spl181.net.srv;

import java.io.FileReader;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Movie {
	private String id;
	private String name;
	private String price;
	private String availableAmount;
	private String totalAmount;
	private ArrayList <String> bannedCountries;
	public Movie (String id,String name,String availableAmount, String price,String totalAmount,ArrayList<String>bannedCountries){
		this.id=id;
		this.name=name;
		this.availableAmount=availableAmount;
		this.price=price;
		this.totalAmount=totalAmount;
		this.bannedCountries=bannedCountries;
	}
	public String getId(){
		return this.id;
	}
	public String getName() {
		return this.name;
	}
	public String getPrice() {
		return this.price;
	}
	public String getAviailableAmount() {
		return this.availableAmount;
	}
	public String getTotalAmount() {
		return this.totalAmount;
	}
	public ArrayList <String> getBannedCountries() {
		return this.bannedCountries;
	}
	 public void setAvailableAmount(String other){
		 this.availableAmount=other;
	 }
	 public void setPrice(String other){
		 this.price=other;
	 }
	 public void setBannedCountries(ArrayList <String> other) {
			this.bannedCountries=other;
		}

}

