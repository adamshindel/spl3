package bgu.spl181.net;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl181.net.srv.SharedData;
import bgu.spl181.net.srv.User;

public class BidiServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SharedData service = SharedData.getData();
		String s = "1=2";
		String[] a = s.split("=");
		for(String s1 : a) {
			System.out.print(s1 + ",");
		}
		System.out.println("   length is " + a.length);
		
		/*User john = new User("john","admin","potato","united states",new ConcurrentHashMap<>(),"0");
		//service.addUser(adam);
		Map<String,String> moviesToAdd = new ConcurrentHashMap<>();
		moviesToAdd.put("0", "movie1");
		moviesToAdd.put("1", "movie2");
		moviesToAdd.put("2", "movie3");
		john.movies = moviesToAdd;
		service.changeMoviesUser(john, moviesToAdd);*/
	}
}
