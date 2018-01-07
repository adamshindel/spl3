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
	public String availableAmount;
	public String totalAmount;
	public ArrayList <String> bannedCountries;
	public Movie (String id,String name,int price, String availableAmount,String totalAmount,ArrayList<String>bannedCountries){
		this.id=id;
		this.name=name;
		this.availableAmount=availableAmount;
		this.price=price;
		this.totalAmount=totalAmount;
		this.bannedCountries=bannedCountries;
	}

}

