package bgu.spl181.net.srv;

import java.io.FileReader;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Movie {
	public String id;
	public String name;
	public int price;
	public int availableAmount;
	public int totalAmount;
	public ArrayList <String> bannedCountries;
	public Movie (String id,String name,int price, int availableAmount,int totalAmount,ArrayList<String>bannedCountries){
		this.id=id;
		this.name=name;
		this.availableAmount=availableAmount;
		this.price=price;
		this.totalAmount=totalAmount;
		this.bannedCountries=bannedCountries;
	}
	 public void setAvailableAmount(int other){
		 this.availableAmount=other;
	 }
	 public void setPrice(int other){
		 this.price=other;
	 }
	

}

